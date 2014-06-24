package com.asb.details;

public class NavDrawerItemDetail {

	private String title;
	private int icon;

	public NavDrawerItemDetail() {
	}

	public NavDrawerItemDetail(String title, int icon) {
		this.title = title;
		this.icon = icon;
	}

	public NavDrawerItemDetail(String title, int icon, String count) {
		this.title = title;
		this.icon = icon;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
}
