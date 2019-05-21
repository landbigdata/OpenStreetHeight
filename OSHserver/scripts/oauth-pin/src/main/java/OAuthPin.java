import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import java.util.Scanner;

public class OAuthPin {
    private static Scanner console = new Scanner(System.in);

    public static void main(String... args) throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(
                // the consumer key of this app
                "R8wMU2ezkp3ijpIcuAnKD4HSRCqfmf69NknPoSNA",
                // the consumer secret of this app
                "40CZ42WcGZQ4PGBEqmi5lkUqrWGIh0xMZd6pZ1ap");

        OAuthProvider provider = new DefaultOAuthProvider(
                "https://www.openstreetmap.org/oauth/request_token",
                "https://www.openstreetmap.org/oauth/access_token",
                "https://www.openstreetmap.org/oauth/authorize");

        /****************************************************
         * The following steps should only be performed ONCE
         ***************************************************/

        // we do not support callbacks, thus pass OOB
        String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

        System.out.println("Please authorize and save PIN-code:" + "\n" + authUrl + "\n");
        System.out.println("Now enter PIN-code: ");

        String pinCode = console.nextLine();

        provider.retrieveAccessToken(consumer, pinCode);

        System.out.print("\n\n");
        System.out.println("Token       : " + consumer.getToken());
        System.out.println("Token secret: " + consumer.getTokenSecret());
    }
}
