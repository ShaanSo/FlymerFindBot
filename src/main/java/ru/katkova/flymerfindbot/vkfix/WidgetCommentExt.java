package ru.katkova.flymerfindbot.vkfix;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.objects.Validable;
import com.vk.api.sdk.objects.annotations.Required;
import com.vk.api.sdk.objects.base.BoolInt;
import com.vk.api.sdk.objects.base.LikesInfo;
import com.vk.api.sdk.objects.base.RepostsInfo;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.wall.CommentAttachment;
import com.vk.api.sdk.objects.wall.PostSource;
import com.vk.api.sdk.objects.widgets.CommentMedia;
import com.vk.api.sdk.objects.widgets.CommentReplies;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WidgetCommentExt implements Validable {
    @SerializedName("attachments")
    private List<CommentAttachment> attachments;
    @SerializedName("can_delete")
    private BoolInt canDelete;
    @SerializedName("comments")
    private CommentReplies comments;
    @SerializedName("date")
    @Required
    private Integer date;
    @SerializedName("from_id")
    private Integer fromId;
    @SerializedName("id")
    @Required
    private Integer id;
    @SerializedName("likes")
    private LikesInfo likes;
    @SerializedName("media")
    private CommentMedia media;
    @SerializedName("post_source")
    private PostSource postSource;
    @SerializedName("post_type")
    private String postType;
    @SerializedName("reposts")
    private RepostsInfo reposts;
    @SerializedName("text")
    @Required
    private String text;
    @SerializedName("to_id")
    private Integer toId;
    @SerializedName("user")
    private UserFull user;

    public WidgetCommentExt() {
    }

    public List<CommentAttachment> getAttachments() {
        return this.attachments;
    }

    public WidgetCommentExt setAttachments(List<CommentAttachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public boolean canDelete() {
        return this.canDelete == BoolInt.YES;
    }

    public BoolInt getCanDelete() {
        return this.canDelete;
    }

    public CommentReplies getComments() {
        return this.comments;
    }

    public WidgetCommentExt setComments(CommentReplies comments) {
        this.comments = comments;
        return this;
    }

    public Integer getDate() {
        return this.date;
    }

    public WidgetCommentExt setDate(Integer date) {
        this.date = date;
        return this;
    }

    public Integer getFromId() {
        return this.fromId;
    }

    public WidgetCommentExt setFromId(Integer fromId) {
        this.fromId = fromId;
        return this;
    }

    public Integer getId() {
        return this.id;
    }

    public WidgetCommentExt setId(Integer id) {
        this.id = id;
        return this;
    }

    public LikesInfo getLikes() {
        return this.likes;
    }

    public WidgetCommentExt setLikes(LikesInfo likes) {
        this.likes = likes;
        return this;
    }

    public CommentMedia getMedia() {
        return this.media;
    }

    public WidgetCommentExt setMedia(CommentMedia media) {
        this.media = media;
        return this;
    }

    public PostSource getPostSource() {
        return this.postSource;
    }

    public WidgetCommentExt setPostSource(PostSource postSource) {
        this.postSource = postSource;
        return this;
    }

    public String getPostType() {
        return this.postType;
    }

    public WidgetCommentExt setPostType(String postType) {
        this.postType = postType;
        return this;
    }

    public RepostsInfo getReposts() {
        return this.reposts;
    }

    public WidgetCommentExt setReposts(RepostsInfo reposts) {
        this.reposts = reposts;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public WidgetCommentExt setText(String text) {
        this.text = text;
        return this;
    }

    public Integer getToId() {
        return this.toId;
    }

    public WidgetCommentExt setToId(Integer toId) {
        this.toId = toId;
        return this;
    }

    public UserFull getUser() {
        return this.user;
    }

    public WidgetCommentExt setUser(UserFull user) {
        this.user = user;
        return this;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.date, this.toId, this.attachments, this.comments, this.postType, this.postSource, this.media, this.fromId, this.canDelete, this.id, this.text, this.user, this.reposts, this.likes});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            WidgetCommentExt widgetComment = (WidgetCommentExt)o;
            return Objects.equals(this.date, widgetComment.date) && Objects.equals(this.attachments, widgetComment.attachments) && Objects.equals(this.comments, widgetComment.comments) && Objects.equals(this.fromId, widgetComment.fromId) && Objects.equals(this.toId, widgetComment.toId) && Objects.equals(this.media, widgetComment.media) && Objects.equals(this.canDelete, widgetComment.canDelete) && Objects.equals(this.postSource, widgetComment.postSource) && Objects.equals(this.postType, widgetComment.postType) && Objects.equals(this.id, widgetComment.id) && Objects.equals(this.text, widgetComment.text) && Objects.equals(this.user, widgetComment.user) && Objects.equals(this.reposts, widgetComment.reposts) && Objects.equals(this.likes, widgetComment.likes);
        } else {
            return false;
        }
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder("WidgetComment{");
        sb.append("date=").append(this.date);
        sb.append(", attachments=").append(this.attachments);
        sb.append(", comments=").append(this.comments);
        sb.append(", fromId=").append(this.fromId);
        sb.append(", toId=").append(this.toId);
        sb.append(", media=").append(this.media);
        sb.append(", canDelete=").append(this.canDelete);
        sb.append(", postSource=").append(this.postSource);
        sb.append(", postType=").append(this.postType);
        sb.append(", id=").append(this.id);
        sb.append(", text='").append(this.text).append("'");
        sb.append(", user=").append(this.user);
        sb.append(", reposts=").append(this.reposts);
        sb.append(", likes=").append(this.likes);
        sb.append('}');
        return sb.toString();
    }

//    public URI getMediaUri() {
//        if (this.getMedia() != null) return this.getMedia().getThumbSrc();
//        else return null;
//    }


}

