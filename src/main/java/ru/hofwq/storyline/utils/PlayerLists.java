package ru.hofwq.storyline.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerLists {
	public static List<UUID> playersToGoOutside = new ArrayList<>();
    public static List<UUID> playersInBlackRoom = new ArrayList<>();
    public static List<UUID> playersWhoTakedKeys = new ArrayList<>();
    public static HashMap<UUID, Integer> playerMessageCount = new HashMap<>();
    public static List<UUID> playersWithLoadedPack = new ArrayList<>();
}
