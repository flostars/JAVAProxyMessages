package platform;

import server.model.PlatformCode;

import java.util.HashMap;
import java.util.Map;

public class PlatformFactory {

    private static Map<PlatformCode, Platform> platforms = new HashMap<>();

    static {
        platforms.put(PlatformCode.CRG, new CraigslistPlatform());
    }

    public static Platform getPlatform(PlatformCode platformCode){
        return platforms.get(platformCode);
    }
}
