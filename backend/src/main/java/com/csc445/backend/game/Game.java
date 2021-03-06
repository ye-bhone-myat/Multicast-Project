package com.csc445.backend.game;

import com.csc445.shared.game.Player;
import com.csc445.shared.game.Spot;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 65;

    private final Spot[][] spots = new Spot[WIDTH][HEIGHT];
    private final List<Player> playersList = new ArrayList<>();

    public Game() {
        initializeSpaces();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                    sweepPlayers();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initializeSpaces() {
        for (int i = 0; i < spots.length; i++) {
            for (int j = 0; j < spots[0].length; j++) {
                spots[i][j] = new Spot(i, j);
            }
        }
    }

    private synchronized boolean playerIsInGame(Player newPlayer) {
        for (Player p : playersList) {
            if (p.getAddress().equals(newPlayer.getAddress())) {
                return true;
            } else if (p.getName().equals(newPlayer.getName())) {
                return true;
            }
        }

        return false;
    }

    public synchronized boolean addPlayerToGame(Player newPlayer) {
        if (!playerIsInGame(newPlayer)) {
            playersList.add(newPlayer);
            System.out.println(newPlayer.getName() + " has connected.");

            return true;
        }

        return false;
    }

    public synchronized boolean updatePlayerHeartbeat(InetAddress address) {
        for (Player p : playersList) {
            if (p.getAddress().equals(address)) {
                p.setHasHeartbeat(true);
                System.out.println(p.getName() + " heartbeat detected.");

                return true;
            }
        }

        return false;
    }

    private synchronized void sweepPlayers() {
        Iterator<Player> playerIterator = playersList.iterator();
        while (playerIterator.hasNext()) {
            final Player player = playerIterator.next();
            if (player.hasHeartbeat()) {
                player.setHasHeartbeat(false);
            } else {
                System.out.println(player.getName() + " has disconnected.");
                playerIterator.remove();
            }
        }
    }

    public synchronized void updateSpot(Spot spotToUpdate) {
        final Spot spot = spots[spotToUpdate.getX()][spotToUpdate.getY()];

        spot.setName(spotToUpdate.getName());
        spot.setColor(spotToUpdate.getColor());

        System.out.println("Spot (" + spot.getX() + "," + spot.getY() + ") updated - Name: " + spot.getName() + " | Color: " + spot.getColor());
    }

    public Spot[][] getSpots() {
        return spots;
    }
}
