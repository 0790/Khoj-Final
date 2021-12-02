package com.example.khoj.Objects;

public class Hostel {
	String name;
	String images_id;
	String address;
	String d_facilities;
	String h_facilities;
	String r_facilities;
	String phone;
	String parent_location;

	double people_adult;
	double people_child;
	double total_available;
	double total_room;
	double price;
	double rating;

	boolean parking;
	boolean wifi;

	public String getName() {
		return name;
	}

	public Hostel setName(String name) {
		this.name = name;
		return this;
	}

	public String getParent_location() {
		return parent_location;
	}

	public Hostel setParent_location(String parent_location) {
		this.parent_location = parent_location;
		return this;
	}

	public String getImages_id() {
		return images_id;
	}

	public Hostel setImages_id(String images_id) {
		this.images_id = images_id;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public Hostel setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getD_facilities() {
		return d_facilities;
	}

	public Hostel setD_facilities(String d_facilities) {
		this.d_facilities = d_facilities;
		return this;
	}

	public String getH_facilities() {
		return h_facilities;
	}

	public Hostel setH_facilities(String h_facilities) {
		this.h_facilities = h_facilities;
		return this;
	}

	public String getR_facilities() {
		return r_facilities;
	}

	public Hostel setR_facilities(String r_facilities) {
		this.r_facilities = r_facilities;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public Hostel setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public double getPeople_adult() {
		return people_adult;
	}

	public Hostel setPeople_adult(double people_adult) {
		this.people_adult = people_adult;
		return this;
	}

	public double getPeople_child() {
		return people_child;
	}

	public Hostel setPeople_child(double people_child) {
		this.people_child = people_child;
		return this;
	}

	public double getTotal_available() {
		return total_available;
	}

	public Hostel setTotal_available(double total_available) {
		this.total_available = total_available;
		return this;
	}

	public double getTotal_room() {
		return total_room;
	}

	public Hostel setTotal_room(double total_room) {
		this.total_room = total_room;
		return this;
	}

	public double getPrice() {
		return price;
	}

	public Hostel setPrice(double price) {
		this.price = price;
		return this;
	}

	public double getRating() {
		return rating;
	}

	public Hostel setRating(double rating) {
		this.rating = rating;
		return this;
	}

	public boolean isParking() {
		return parking;
	}

	public Hostel setParking(boolean parking) {
		this.parking = parking;
		return this;
	}

	public boolean isWifi() {
		return wifi;
	}

	public Hostel setWifi(boolean wifi) {
		this.wifi = wifi;
		return this;
	}

}
