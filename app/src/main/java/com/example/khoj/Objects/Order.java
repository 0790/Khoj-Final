package com.example.khoj.Objects;

public class Order {
	long beds_available;
	int status;
	long check_in, check_out, order_id, price_total;
	String hostel_name;
	String images_id;
	String order_loc;

	public String getOrder_loc() {
		return order_loc;
	}

	public Order setOrder_loc(String order_loc) {
		this.order_loc = order_loc;
		return this;
	}

	public String getImages_id() {
		return images_id;
	}

	public Order setImages_id(String images_id) {
		this.images_id = images_id;
		return this;
	}


	public long getBeds_available() {
		return beds_available;
	}

	public Order setBeds_available(Long beds_available) {
		this.beds_available = beds_available;
		return this;
	}


	public int getStatus() {
		return status;
	}

	public Order setStatus(int status) {
		this.status = status;
		return this;
	}

	public long getCheck_in() {
		return check_in;
	}

	public Order setCheck_in(long check_in) {
		this.check_in = check_in;
		return this;
	}

	public long getCheck_out() {
		return check_out;
	}

	public Order setCheck_out(long check_out) {
		this.check_out = check_out;
		return this;
	}

	public long getOrder_id() {
		return order_id;
	}

	public Order setOrder_id(long order_id) {
		this.order_id = order_id;
		return this;
	}

	public long getPrice_total() {
		return price_total;
	}

	public Order setPrice_total(long price_total) {
		this.price_total = price_total;
		return this;
	}

	public String getHostel_name() {
		return hostel_name;
	}

	public Order setHostel_name(String hostel_name) {
		this.hostel_name = hostel_name;
		return this;
	}


}