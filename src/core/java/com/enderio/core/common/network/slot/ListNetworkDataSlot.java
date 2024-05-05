package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListNetworkDataSlot<T, V extends Tag> extends NetworkDataSlot<List<T>> {

    private final Function<T, V> serializer;
    private final Function<V, T> deSerializer;
    private final BiConsumer<T, FriendlyByteBuf> toBuffer;
    private final Function<FriendlyByteBuf, T> fromBuffer;

    public ListNetworkDataSlot(Supplier<List<T>> getter, Consumer<List<T>> setter,
        Function<T, V> serializer, Function<V, T> deSerializer, BiConsumer<T, FriendlyByteBuf> toBuffer, Function<FriendlyByteBuf, T> fromBuffer) {
        //I can put null here, because I override the only usage of the setter
        super(getter, setter);
        this.serializer = serializer;
        this.deSerializer = deSerializer;
        this.toBuffer = toBuffer;
        this.fromBuffer = fromBuffer;
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, List<T> value) {
        ListTag listTag = new ListTag();
        for (T t : value) {
            listTag.add(serializer.apply(t));
        }
        return listTag;
    }

    @Override
    protected List<T> valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        if (nbt instanceof ListTag listTag) {
            List<T> list = new ArrayList<>();
            for (Tag tag : listTag) {
                list.add(deSerializer.apply((V) tag));
            }
            return list;
        } else {
            throw new IllegalStateException("Invalid list tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, List<T> value) {
        buf.writeInt(value.size());
        for (T element: value) {
            toBuffer.accept(element, buf);
        }
    }

    @Override
    public List<T> valueFromBuffer(RegistryFriendlyByteBuf buf) {
        List<T> list = new ArrayList<>();
        try {
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                list.add(fromBuffer.apply(buf));
            }
            return list;
        } catch (Exception e) {
            throw new IllegalStateException("Invalid list buffer was passed over the network.");
        }
    }
}
