package edu.umkc.ase.cinemetrics.constants;

/**
 * Created by Arun on 06-09-2015.
 */
public class AppConstants {

    public enum SharedPreferenceKeys {
        USER_NAME("userName"),
        USER_EMAIL("userEmail"),
        USER_IMAGE_URL("userImageUrl");


        private String value;

        SharedPreferenceKeys(String value) {
            this.value = value;
        }

        public String getKey() {
            return value;
        }
    }


    public static final String GOOGLE_CLIENT_ID = "806651044065-lbarqa7hjf35j3j4l6r1g6vp4up5ku13.apps.googleusercontent.com";
}
