package Model;

public class Post {
    int id;
    int ownerId;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public Post(int id, int ownerId) {
        this.id = id;
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Post(id=" + id + ", owner_id=" + ownerId + ")";
    }
}