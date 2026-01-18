package com.purityvanilla.pvchat.listeners.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.purityvanilla.pvchat.PVChat;

import java.util.ArrayList;
import java.util.List;

public class EditBookListener implements PacketListener {
    private final PVChat plugin;

    public EditBookListener(PVChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!plugin.config().isContentFilterEnabled()) return;
        if (event.getPacketType() != PacketType.Play.Client.EDIT_BOOK) return;
        WrapperPlayClientEditBook packet = new WrapperPlayClientEditBook(event);

        List<String> pages = packet.getPages();
        if (pages == null) return;

        List<String> filteredPages = new ArrayList<>();
        for (String page : pages) {
            filteredPages.add(plugin.getTextFilter().filterText(page));
        }

        packet.setPages(filteredPages);
    }
}
