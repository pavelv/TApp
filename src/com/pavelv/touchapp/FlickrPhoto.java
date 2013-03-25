package com.pavelv.touchapp;

class FlickrPhoto {
	String id;
	String owner;
	String secret;
	String server;
	String title;
	String farm;

	public FlickrPhoto(String _id, String _secret, String _server, String _farm) {
		id = _id;
		secret = _secret;
		server = _server;
		farm = _farm;
	}

	public String makeURL() {
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id
				+ "_" + secret + "_s.jpg";
	}

	public String makeURL2() {
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id
				+ "_" + secret + "_b.jpg";
	}
	
	public String makeURL3() {
		return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id
				+ "_" + secret + "_z.jpg";
	}
}