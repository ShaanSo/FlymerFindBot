package ru.katkova.flymerfindbot.vkfix;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.objects.Validable;
import com.vk.api.sdk.objects.annotations.Required;
import java.util.List;
import java.util.Objects;

public class GetCommentsResponseExt implements Validable {
    @SerializedName("count")
    @Required
    private Integer count;
    @SerializedName("posts")
    @Required
    private List<WidgetCommentExt> posts;

    public GetCommentsResponseExt() {
    }

    public Integer getCount() {
        return this.count;
    }

    public GetCommentsResponseExt setCount(Integer count) {
        this.count = count;
        return this;
    }

    public List<WidgetCommentExt> getPosts() {
        return this.posts;
    }

    public GetCommentsResponseExt setPosts(List<WidgetCommentExt> posts) {
        this.posts = posts;
        return this;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.count, this.posts});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            GetCommentsResponseExt getCommentsResponse = (GetCommentsResponseExt)o;
            return Objects.equals(this.count, getCommentsResponse.count) && Objects.equals(this.posts, getCommentsResponse.posts);
        } else {
            return false;
        }
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder("GetCommentsResponse{");
        sb.append("count=").append(this.count);
        sb.append(", posts=").append(this.posts);
        sb.append('}');
        return sb.toString();
    }
}
