package base.obj;

public class FullTrack {
	
	private Station[] stations;
	private Track[] tracks;
	
	public FullTrack(Station[] stations, Track[] tracks) {
		this.stations = stations;
		this.tracks = tracks;
	}
	
	public Station[] getStations() {
		return this.stations;
	}
	
	public Track[] getTracks() {
		return this.tracks;
	}
}
