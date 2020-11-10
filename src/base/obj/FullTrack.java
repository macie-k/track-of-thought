package base.obj;

import java.util.List;

public class FullTrack {
	
	private List<Station> stations;
	private List<Track> tracks;
	private List<Ball> balls;
	
	public FullTrack(List<Station> stations, List<Track> tracks, List<Ball> balls) {
		this.stations = stations;
		this.tracks = tracks;
		this.balls = balls;
	}
	
	public Track findTrack(int col, int row) {
		for(Track track : tracks) {
			if(track.getColumn() == col && track.getRow() == row) {
				return track;
			}
		}
		return null;
	}
	
	public Station findStation(int col, int row) {
		for(Station station : stations) {
			if(station.getColumn() == col && station.getRow() == row) {
				return station;
			}
		}
		return null;
	}
	
	public List<Ball> getBalls() {
		return this.balls;
	}
	
	public List<Station> getStations() {
		return this.stations;
	}
	
	public List<Track> getTracks() {
		return this.tracks;
	}
}
