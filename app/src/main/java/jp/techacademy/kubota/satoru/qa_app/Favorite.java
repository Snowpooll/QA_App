package jp.techacademy.kubota.satoru.qa_app;

import java.io.Serializable;

/**
 * Created by snowpool on 17/02/13.
 */

public class Favorite implements Serializable {
    private String FavoriteKey;
    private String FavoriteQuestionId;

    public String getFavoriteKey() {
        return FavoriteKey;
    }

    public String getFavoriteQuestionId() {
        return FavoriteQuestionId;
    }

    public void setFavoriteKey(String favoriteKey) {
        FavoriteKey = favoriteKey;
    }

    public void setFavoriteQuestionId(String favoriteQuestionId) {
        FavoriteQuestionId = favoriteQuestionId;
    }

    public Favorite(String favoriteKey, String favoriteQuestionId) {
        FavoriteKey = favoriteKey;
        FavoriteQuestionId = favoriteQuestionId;
    }
}
