package config;

import mail.MailIdentifierStrategy;
import mail.SimpleMailIdentifierStrategy;

public class SystemConfiguration {
	public static final String apiKeyForEmailRPC="db6f2e20105511e6b7006994045bc4f1";
    public static String craigslistPropagatorEndpoint =
    		"http://rpc.datafall.net:8888/client/dougsherk/schedule";
//    http://rpc.datafall.net:8888/client/dougsherk/schedule?url=https://warsaw.craigslist.pl/msd/5602191427.html&
    public static String deployedAt = "http://ec2-52-41-111-111.us-west-2.compute.amazonaws.com:8102";

    public static MailIdentifierStrategy emailIdentifierStrategy = new SimpleMailIdentifierStrategy();
}
