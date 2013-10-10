package com.paymill.android.samples.vouchermill.ui.helpers;

import java.lang.ref.WeakReference;

import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;

public class SherlockListNavigationHelper<E extends Enum<E> & SherlockListNavigationHelper.Item>
		implements OnNavigationListener {

	private WeakReference<SupportActionBarActivity> activityReference;
	private E selectedItem;
	private E[] list;
	private WeakReference<Listener<E>> listenerReference;
	private ArrayAdapter<String> adapter;
	private int textViewResourceId;
	private int dropDownViewResource;

	private static final int DEFAULT_SELECTED_ITEM = 0;

	public interface Item {
		public int getStringResource();
	}

	public interface Listener<L> {
		public boolean onNavigationItemSelected(L selected);
	}

	public interface SupportActionBarActivity {
		public ActionBar getSupportActionBar();
	}

	public SherlockListNavigationHelper(SupportActionBarActivity activity,
			E[] list, Listener<E> listener, int textViewResourceId,
			int dropDownViewResource, int defaultSelectedItem) {
		this.activityReference = new WeakReference<SherlockListNavigationHelper.SupportActionBarActivity>(
				activity);
		this.list = list;
		this.listenerReference = new WeakReference<SherlockListNavigationHelper.Listener<E>>(
				listener);
		this.textViewResourceId = textViewResourceId;
		this.dropDownViewResource = dropDownViewResource;
	}

	public SherlockListNavigationHelper(SupportActionBarActivity activity,
			E[] list, Listener<E> listener, int textViewResourceId,
			int dropDownViewResource) {
		this(activity, list, listener, textViewResourceId,
				dropDownViewResource, DEFAULT_SELECTED_ITEM);
	}

	public SherlockListNavigationHelper(SupportActionBarActivity activity,
			E[] list, Listener<E> listener, int textViewResourceId,
			int dropDownViewResource, E item) {
		this(activity, list, listener, textViewResourceId,
				dropDownViewResource, itemToPosition(list, item));
	}

	public E getSelectedItem() {
		return selectedItem;
	}

	public void enableListNavigation() {
		if (activityReference.get() != null) {
			adapter = new ArrayAdapter<String>(activityReference.get()
					.getSupportActionBar().getThemedContext(),
					textViewResourceId, getStringArray());
			adapter.setDropDownViewResource(dropDownViewResource);
			activityReference.get().getSupportActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			activityReference.get().getSupportActionBar()
					.setListNavigationCallbacks(adapter, this);
			selectedItem = list[0];
		}
	}

	public void disableListNavigation() {
		if (activityReference.get() != null) {
			activityReference.get().getSupportActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
	}

	public void setSelectedNavigationItem(int position) {
		activityReference.get().getSupportActionBar()
				.setSelectedNavigationItem(position);

	}

	public void setSelectedNavigationItem(E selectedItem) {
		activityReference.get().getSupportActionBar()
				.setSelectedNavigationItem(itemToPosition(list, selectedItem));

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		selectedItem = list[itemPosition];
		listenerReference.get().onNavigationItemSelected(selectedItem);
		return false;
	}

	private String[] getStringArray() {
		if (activityReference.get() != null) {
			String[] result = new String[list.length];
			for (int i = 0; i < list.length; i++) {
				result[i] = activityReference.get().getSupportActionBar()
						.getThemedContext().getResources()
						.getString(list[i].getStringResource());
			}
			return result;
		} else {
			return null;
		}
	}

	private static <T> int itemToPosition(T[] list, T item) {
		for (int i = 0; i < list.length; i++) {
			if (item == list[i]) {
				return i;
			}
		}
		return -1;
	}
}
