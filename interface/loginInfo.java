public class loginInfo {
    private int userID;
    private String restaurantID;
    private String username;
    private String position;

    public loginInfo(int userID, String restaurantID, String username, String position){
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.username = username;
        this.position = position;
    }

    public int getUserID(){
        return userID;
    }

    public String getRestaurantID(){
        return restaurantID;
    }

    public String getUsername(){
        return username;
    }

    public String getPosition(){
        return position;
    }
}
