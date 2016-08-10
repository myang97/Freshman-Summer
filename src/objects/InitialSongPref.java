package objects;

//this class is where the information grabbed from the prefevents table is compiled
public class InitialSongPref implements Comparable<InitialSongPref>{
	private String songName;
	private String type;
	private double totalListened;
	private double duration;
	private int upVote;
	private int downVote;
	private int rating;
	private int skipCounter;

	//there are two different kinds of constructors
	//1. when the next/previous buttons are pressed, so there isn't a vote
	//2. when the upvote/downvote buttons are pressed, so there isn't a listenedamt
	public InitialSongPref(String songName, double listenedAmt, double duration, String type){
		this.songName = songName;
		this.duration = duration;
		this.upVote = 0;
		this.downVote = 0;
		this.rating = 0;
		this.type = type;
		listenedRatingChange(listenedAmt);
	}
	
	public InitialSongPref(String songName, int upVote, int downVote, String type){
		this.songName = songName;
		this.upVote = upVote;
		this.downVote = downVote;
		this.rating = 0;
		this.type = type;
		votingRatingChange(upVote, downVote);
	}
	
	public InitialSongPref(String songName, String type){
		this.songName = songName;
		this.upVote = 0;
		this.downVote = 0;
		this.rating = 0;
		this.type = type;
		updateClickRating();
	}
	
	public double getTotalListenedAmount(){
		return totalListened;
	}
	
	public void updateClickRating(){
		rating += 50;
	}
	
	//this method is called everytime a new listenedamt is inputted
	//the skipcounter checks if the amount of time is negligible
	//all other times will affect the rating
	//type can only be up, down, nextSong, previousSong, nextPlaylist, previousPlaylist and click
	public void listenedRatingChange(double listenedAmt){
		totalListened += listenedAmt;
		if(listenedAmt < 5){
			skipCounter++;
			if(skipCounter == 20){
				rating += -50;
				skipCounter = 0;
			}
		}
		else if(listenedAmt >= 5 && listenedAmt < 30){
			rating += -10;
		}
		else if(listenedAmt >= 30 && listenedAmt < 60){
			rating += 10;
		}
		else if(listenedAmt >= 60 && listenedAmt < (duration - 30)){
			rating += 20;
		}
		else{
			rating += 30;
		}
	}
	
	//this method is called everytime a new vote is inputted by the method below and in the constructor
	public void votingRatingChange(int upVote, int downVote){
		rating += upVote * 500;
		rating += downVote * -500;
	}
	
	//this method is called from preftable whenever there's a vote row and the songname is already present
	public void updateVotes(int upVote, int downVote){
		this.upVote += upVote;
		this.downVote += downVote;
		votingRatingChange(upVote, downVote);
	}
	
	//this method is called from preftable like above, if the songname is already present
	public void updateTimes(double listenedAmt, double duration){
		//this.listenedAmt.add(listenedAmt);
		listenedRatingChange(listenedAmt);
		this.duration = duration;
	}
	
	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public void setListenedAmt(double listenedAmt) {
		listenedRatingChange(listenedAmt);
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public int getUpVote() {
		return upVote;
	}

	public void setUpVote(int upVote) {
		this.upVote = upVote;
	}

	public int getDownVote() {
		return downVote;
	}

	public void setDownVote(int downVote) {
		this.downVote = downVote;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
 
	//this is used for Collections.sort, so that the list will have the songs listed in rating descending order
	public int compareTo(InitialSongPref compareSong) {
		if(this.rating == compareSong.rating){
			return compareSong.skipCounter - this.skipCounter;
		}
		//descending order
		return compareSong.rating - this.rating;
		//ascending order
		//return compareId - this.listId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
}
