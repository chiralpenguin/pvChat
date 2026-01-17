package com.purityvanilla.pvchat.listeners.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.purityvanilla.pvchat.PVChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import java.util.Optional;

public class EntityMetaDataListener implements PacketListener {
    private final PVChat plugin;

    public EntityMetaDataListener(PVChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

        for (EntityData metadata : packet.getEntityMetadata())
            if (metadata.getIndex() == 2 && metadata.getValue() instanceof Optional<?> opt) {
                if (opt.isPresent() && opt.get() instanceof Component name) {
                    Component filtered = plugin.getTextFilter().filterComponent(name);
                    metadata.setValue(Optional.of(filtered));
                }
            }
    }
}
