package vn.edu.usth.x.SearchPage;

public class searchItem {
    private String topic;
    private String content;
    private String post;

    public searchItem(String topic, String content, String post) {
        this.topic = topic;
        this.content = content;
        this.post = post;
    }

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public String getPost() {
        return post;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPost(String post) {
        this.post = post;
    }
}

