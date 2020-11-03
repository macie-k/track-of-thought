package base.obj;

public class FullTrack {
	
	private Station[] stations;
	private Track[] tracks;
	
	public FullTrack(Station[] stations, Track[] tracks) {
		this.stations = stations;
		this.tracks = tracks;
	}
	
	public Track findTrack(int col, int row) {
		for(Track track : tracks) {
			if(track.getColumn() == col && track.getRow() == row) {
				return track;
			}
		}
		return null;
	}
	
	public Station[] getStations() {
		return this.stations;
	}
	
	public Track[] getTracks() {
		return this.tracks;
	}
}
