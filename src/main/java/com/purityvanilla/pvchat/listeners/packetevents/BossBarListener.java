package com.purityvanilla.pvchat.listeners.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBossBar;
import com.purityvanilla.pvchat.PVChat;
import net.kyori.adventure.text.Component;

public class BossBarListener implements PacketListener {
    private final PVChat plugin;

    public BossBarListener(PVChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.BOSS_BAR) return;
        WrapperPlayServerBossBar packet = new WrapperPlayServerBossBar(event);

        if (packet.getAction() != WrapperPlayServerBossBar.Action.ADD
                && packet.getAction() != WrapperPlayServerBossBar.Action.UPDATE_TITLE
        ) return;

        Component title = packet.getTitle();
        Component filtered = plugin.getTextFilter().filterComponent(title);
        packet.setTitle(filtered);
    }
}
