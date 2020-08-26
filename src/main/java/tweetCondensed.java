//a quick placeholder class so I don't have to deal with the twitter4j library built in stuff
public class tweetCondensed {

    private String text;
    private Long id;
    private Integer score;

    public tweetCondensed(String text, Long id) {
        this.text = text;
        this.id = id;
        this.score = 0;
    }

    public tweetCondensed(String text, Long id, Integer score) {
        this.text = text;
        this.id = id;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "tweetCondensed{" +
                "text='" + text + '\'' +
                ", id=" + id +
                ", score=" + score +
                '}';
    }
}
