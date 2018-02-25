package com.dmitriymorozov.findfork.util;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.dmitriymorozov.findfork.database.DBContract;
import com.dmitriymorozov.findfork.model.explorePOJO.ItemsItem;
import com.dmitriymorozov.findfork.model.explorePOJO.Venue;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.List;

import static com.dmitriymorozov.findfork.util.Constants.*;

public class Util {



		/**
		 * Calculates distance in meters between two points
		 */
		public static int calculateDistance(LatLng point1, LatLng point2) {
				double lat1 = point1.latitude;
				double lng1 = point1.longitude;
				double lat2 = point2.latitude;
				double lng2 = point2.longitude;
				double dLat = Math.toRadians(lat2-lat1);
				double dLng = Math.toRadians(lng2-lng1);
				double a =  Math.sin(dLat/2) * Math.sin(dLat/2) +
						Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
								Math.sin(dLng/2) * Math.sin(dLng/2);
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
				return  (int) (EARTH_RADIUS * c);
		}

		/**
		 * Utility method to expand the provided LatLng rectangle by the given coefficient
		 */
		public static LatLngBounds expandRegionBy(LatLngBounds bounds, double coef) {
				double south = bounds.southwest.latitude;
				double north = bounds.northeast.latitude;
				double west = bounds.southwest.longitude;
				double east = bounds.northeast.longitude;
				double height = Math.abs(north - south);
				double width = Math.abs(west - east);
				double proportion = height / width;
				double expandedArea = height * width * coef;
				double expandedHeight = Math.sqrt(expandedArea * proportion);
				double expandedWidth = Math.sqrt(expandedArea / proportion);
				double widthDelta = (expandedWidth - width) / 2;
				double heightDelta = (expandedHeight - height) / 2;
				return new LatLngBounds(new LatLng(south - heightDelta, west - widthDelta), new LatLng(north + heightDelta, east + widthDelta));
		}

		/**
		 * Utility method to convert List<ItemsItem> into venues ContentValues[] structure
		 */
		public static ContentValues[] createVenuesContentValuesArray(@NonNull List<ItemsItem> venueList){
				int venueListSize = venueList.size();
				ContentValues[] contentValues = new ContentValues[venueListSize];
				for (int i = 0; i < venueListSize; i++) {
						Venue singleVenue = venueList.get(i).getVenue();
						String venueId = singleVenue.getId();
						String venueName = singleVenue.getName();
						double latitude = singleVenue.getLocation().getLat();
						double longitude = singleVenue.getLocation().getLng();
						Double rating = singleVenue.getRating();

						contentValues[i] = new ContentValues();
						contentValues[i].put(DBContract.VENUE_ID, venueId);
						contentValues[i].put(DBContract.VENUE_NAME, venueName);
						contentValues[i].put(DBContract.VENUE_LAT, latitude);
						contentValues[i].put(DBContract.VENUE_LNG, longitude);
						contentValues[i].put(DBContract.VENUE_RATING, rating);
				}
				return contentValues;
		}

		/**
		 * Utility method to convert List<ItemsItem> into details ContentValues[] structure
		 */
		public static ContentValues[] createDetailsContentValuesArray(@NonNull List<ItemsItem> venueList){
				int venueListSize = venueList.size();
				ContentValues[] contentValues = new ContentValues[venueListSize];
				for (int i = 0; i < venueListSize; i++) {
						Venue singleVenue = venueList.get(i).getVenue();
						String venueId = singleVenue.getId();
						String addressFormatted = null;
						String phone = null;
						String phoneFormatted = null;
						String siteUrl = singleVenue.getUrl();
						Integer priceTier = null;
						String priceCurrency = null;
						String priceMessage = null;
						if(singleVenue.getLocation() != null && singleVenue.getLocation().getFormattedAddress() != null){
								addressFormatted = formattedAddressToString(singleVenue.getLocation().getFormattedAddress());
						}
						if(singleVenue.getContact() != null){
								phone = singleVenue.getContact().getPhone();
								phoneFormatted = singleVenue.getContact().getFormattedPhone();
						}
						if(singleVenue.getPrice() != null){
								priceTier = singleVenue.getPrice().getTier();
								priceCurrency = singleVenue.getPrice().getCurrency();
								priceMessage = singleVenue.getPrice().getMessage();
						}

						contentValues[i] = new ContentValues();
						contentValues[i].put(DBContract.VENUE_ID, venueId);
						contentValues[i].put(DBContract.DETAILS_ADDRESS_FORMATTED, addressFormatted);
						contentValues[i].put(DBContract.DETAILS_PHONE, phone);
						contentValues[i].put(DBContract.DETAILS_PHONE_FORMATTED, phoneFormatted);
						contentValues[i].put(DBContract.DETAILS_SITE_URL, siteUrl);
						contentValues[i].put(DBContract.DETAILS_PRICE_TIER, priceTier);
						contentValues[i].put(DBContract.DETAILS_PRICE_CURRENCY, priceCurrency);
						contentValues[i].put(DBContract.DETAILS_PRICE_MESSAGE, priceMessage);
				}
				return contentValues;
		}

		/**
		 * Convert List<String> of formatted address details into a single String
		 */
		private static String formattedAddressToString(List<String> formattedAddress){
				int listSize = formattedAddress.size();
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < listSize - 1; i++) {
						stringBuilder.append(formattedAddress.get(i));
						stringBuilder.append("\n");
				}
				stringBuilder.append(formattedAddress.get(listSize-1));
				return stringBuilder.toString();
		}
}