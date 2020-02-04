package lunabot.twitch;

import javax.sound.sampled.Port.Info;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.DonationEvent;
import com.github.twitch4j.chat.events.channel.FollowEvent;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.common.events.channel.ChannelGoLiveEvent;
import com.github.twitch4j.common.events.channel.ChannelGoOfflineEvent;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;

import lunabot.gateway.Gateway;
import lunabot.gateway.Log;

public class LunaTwitchClient implements Runnable {

	/**
	 * @author SirMangler
	 * 22nd Feburary 2019
	 */
	
	public static TwitchClient cl;
	final private static Log log = new Log("Twitch-Alerts");
	//
	
	@Override
	public void run() {	
		log.info("Starting Twitch");

		
		
		cl = TwitchClientBuilder.builder()
				.withEnableChat(true)
				.withChatAccount(new OAuth2Credential("twitch", "y5scyy14mxmlgffbetlg9ajzeipo23"))
				.withEnablePubSub(true)
				.withEnableHelix(true)
				.withClientId("294fvy3epe78czy1bvi8rilcw5x0ai")
				.withClientSecret("u3fowtlpro50ks5j6fe5pqj80pfynn")
				.build();	
		
		cl.getChat().connect();
		cl.getChat().joinChannel("kelsilynstar");
		
		cl.getEventManager().onEvent(DonationEvent.class).subscribe(e -> onDonationEvent(e));
		cl.getEventManager().onEvent(FollowEvent.class).subscribe(e -> onFollowEvent(e));
		cl.getEventManager().onEvent(GiftSubscriptionsEvent.class).subscribe(e -> onGiftSubscriptionsEvent(e));
		cl.getEventManager().onEvent(SubscriptionEvent.class).subscribe(e -> onSubscriptionEvent(e));
		cl.getEventManager().onEvent(ChannelGoLiveEvent.class).subscribe(e -> onGoLiveEvent(e));
		cl.getEventManager().onEvent(ChannelGoOfflineEvent.class).subscribe(e -> onGoOfflineEvent(e));
		cl.getEventManager().onEvent(PrivateMessageEvent.class).subscribe(e -> onPMEvent(e));
		//cl.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(e -> onChannelMessageEvent(e));
		
		cl.getClientHelper().enableStreamEventListener("kelsilynstar");
		cl.getClientHelper().enableFollowEventListener("kelsilynstar");
		
		log.info("Twitch Initialised");
	}
	
	boolean live = false;
	
	public void onDonationEvent(DonationEvent e) {
		String m = e.getUser().getName()+" has just donated "+e.getCurrency().getSymbol()+e.getAmount()+"!! Thanks!";
		Gateway.sendDAlert(m);
		
		log.info(m);
	}
	
	public void onFollowEvent(FollowEvent e) {
		String m = e.getUser().getName()+" has just followed!";
		Gateway.sendDAlert(m);
		
		log.info(m);
	}
		
	public void onGiftSubscriptionsEvent(GiftSubscriptionsEvent e) {
		String m = e.getUser()+" has just gifted a subscription!";
		Gateway.sendDAlert(m);
		
		log.info(m);
	}
	
	public void onSubscriptionEvent(SubscriptionEvent e) {
		String msg;
		int m = e.getMonths();
		
		if (e.getGifted()) {
			if (m < 1) {
				msg = e.getGiftedBy().getName()+" has just gifted a subscription of "+e.getSubscriptionPlan()+" to "+e.getUser().getName()+"! Thanks!";
			} else {
				msg = e.getGiftedBy().getName()+" has just gifted a resubscription of "+e.getSubscriptionPlan()+" to "+e.getUser().getName()+"! Thanks!";
			}
		} else {
			if (m < 1) {
				msg = e.getUser().getName()+"has just subscribed! Thanks!";
			} else {
				msg = e.getUser().getName()+" has just resubscribed for "+m+" months! Thanks!";
			}
		}
		
		
		Gateway.sendDAlert(msg);
		log.info(msg);
	}
	
	public void onGoLiveEvent(ChannelGoLiveEvent e) {
		Gateway.sendDAlert("Star has just gone live!!\n "+e.getTitle());
		live=true;
		
		log.info("Kelsilynstar LIVE");
	}
	
	public void onGoOfflineEvent(ChannelGoOfflineEvent e) {
		live=false;
		
		log.info("Kelsilynstar OFFLINE");
	}
	
	//
	//public void onChannelChangeGameEvent(ChannelChangeGameEvent e) {
		//gate.sendDAlert("Star is now playing "+e.);
	//}
	
	public void onPMEvent(PrivateMessageEvent e) {
		log.info("DM: "+e.getUser().getName()+"> "+e.getMessage());
	}
	
	public void onChannelMessageEvent(ChannelMessageEvent e) {
		log.info(e.getMessage());
	}
}
