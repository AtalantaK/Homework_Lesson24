package entities;

public class Task {
    private int id;

    private String title;

    private String completed;

    public Task(int id, String title, String completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public Task() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = "" + completed;
    }

    @Override
    public String toString() {
        return "entities.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
