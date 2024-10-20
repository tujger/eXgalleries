package com.edeqa.exgalleries.helpers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.edeqa.exgalleries.Main;
import com.edeqa.exgalleries.PictureActivity;
import com.edeqa.exgalleries.R;

public class ItemAdapter extends EntityAdapter<Item> {

	final public static String TABLE_NAME = "item";

	// static Context context;
	@SuppressLint("UseSparseArrays")
	private static Map<Long, ItemAdapter> pool = new HashMap<Long, ItemAdapter>();
	private boolean header = false;

	public static ItemAdapter getInstance(Context context, long parentId) {
		if (pool.containsKey(parentId)) {
			return pool.get(parentId);
		} else {
			ItemAdapter pa = new ItemAdapter(context);
			pa.setActiveParent(parentId);
			pool.put(parentId, pa);
			return pa;
		}
	}

	private ItemAdapter(Context context) {
		super(context);
		// this.context = context;
		// // loader=PictureLoader.getInstance(ctx);

	}

	@Override
	protected Item getEntity() {
		return new Item();
	}

	public static void clear() {
		pool.clear();
		pool = null;
	}

	@Override
	public Entity<?> getItem(Entity<?> item, Cursor cursor) {
		return getItem(item, cursor, true);
		// return ((Item) getItem(item, cursor,
		// true)).setFailed(cursor.getInt(COL_FAILED) > 0 ? true : false)
		// .setAlbum(cursor.getInt(COL_ALBUM) > 0 ? true :
		// false).setAlbumId(cursor.getLong(COL_ALBUM_ID));
	}

	public void release(long id) {
		// pool.clear();
		// if (true)
		// return;
		//
		// long id = 0;
		Cursor cur = database.query(getTableName(), null, KEY_PARENT_ID + " = ?", new String[] { id + "" }, null, null,
				null);
		if (cur.moveToFirst()) {
			while (!cur.isAfterLast()) {
				release(cur.getLong(cur.getColumnIndex(KEY_ID)));
				cur.moveToNext();
			}
		}
		cur.close();

		if (id > 0) {
			ItemAdapter ia = pool.get(id);
			ia = null;
			pool.remove(id);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Entity<?> item;
		final View view;
		int i;

		if (null == convertView) {
			// view = mView;
			view = View.inflate(context, mView, null);
		} else {
			view = convertView;
		}

		item = getItem(position);

		if (isHeader())
			view.setMinimumHeight((int) context.getResources().getDimension(R.dimen.gallery_header_height));
		else if (getActiveParent() > 0)
			view.setMinimumHeight(getThumbOptimalSize());
		else
			view.setMinimumHeight((int) context.getResources().getDimension(R.dimen.library_header_height));

		if (item.isFailed()) {
			updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_FAILED, false);
			refresh();
		}

		if (view.findViewById(R.id.ivThumb) != null) {
			// loader.put(item).load(context,rl, item.getId());
			callPicasso(item, view);
		}

		int height = getThumbOptimalSize() / 3+2;
		float textSize = height / 3;
		Button b = view.findViewById(R.id.bReload);
		if (b != null) {
			b.setHeight(height);
			if (b.getTextSize() > textSize)
				b.setTextSize(textSize);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateItemValueNoRefresh(item.getParentId(), KEY_UPDATE_DATE, new Date());
					((ViewSwitcher) view).showPrevious();
					callPicasso(item, view);
				}
			});
		}

		b = view.findViewById(R.id.bRemove);
		if (b != null) {
			b.setHeight(height);
			if (b.getTextSize() > textSize)
				b.setTextSize(textSize);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateItemValueNoRefresh(item.getParentId(), KEY_UPDATE_DATE, new Date());
					view.findViewById(R.id.rlError).setVisibility(View.INVISIBLE);
					new ClearChildrenTask((Item) item, true).execute();
					dismissItemRecursively(item);
					((ViewSwitcher) view).showPrevious();
				}
			});
		}

		b = view.findViewById(R.id.bSkip);
		if (b != null) {
			b.setHeight(height);
			if (b.getTextSize() > textSize)
				b.setTextSize(textSize);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((ViewSwitcher) view).showPrevious();
				}
			});
		}

		if (isHeader()) {
			height = ((int) context.getResources().getDimension(R.dimen.gallery_header_height) - 3) / 3;
			textSize = height / 3;

			b = view.findViewById(R.id.bAllShows);
			if (b != null) {
				b.setHeight(height);
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						updateAllItemsValueIfEmpty(item.getId(), KEY_ACCESS_DATE, new Date());
						getInstance(context, item.getId()).refresh();
						((ViewSwitcher) view).showPrevious();
						// ItemAdapter.getInstance(context,
						// item.getId()).refresh();
						// refresh();
						// TextView tv = (TextView)
						// view.findViewById(R.id.tvNewCounter);
						// if (tv != null) {
						// int i = pa.getAllNewEntries(item.getId()).getCount();
						// if (i > 0) {
						// tv.setText(Html.fromHtml(context.getString(R.string.counter_new,
						// i)));
						// tv.setVisibility(View.VISIBLE);
						// } else {
						// tv.setVisibility(View.GONE);
						// }
						// }
					}
				});
			}

			b = view.findViewById(R.id.bRemoveFaulty);
			if (b != null) {
				b.setHeight(height);
				if (b.getTextSize() > textSize)
					b.setTextSize(textSize);
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						removeFault(item.getId());
						((ViewSwitcher) view).showPrevious();
					}
				});
			}

			b = view.findViewById(R.id.bClear);
			if (b != null) {
				b.setHeight(height);
				if (b.getTextSize() > textSize)
					b.setTextSize(textSize);
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// updateAllItemsValueIfEmpty(item.getId(),
						// KEY_ACCESS_DATE, new Date());
						// getInstance(item.getId()).refresh();

						new ClearChildrenTask((Item) item, true).execute();

						Main.getMainActivity().onNavigationDrawerItemSelected(item.getParentId(), item.getId(), 0);

						((ViewSwitcher) view).showPrevious();
					}
				});
			}
		}
		RatingBar rb = view.findViewById(R.id.rbRating);
		if (rb != null) {
			if (isHeader() && !isFirstLevelItem(item)) {
				rb.setNumStars(5);
				rb.setRating(item.getRating());

				rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
						if (fromUser) {
							item.setRating((int) rating);
							updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_RATING, (int) rating);
						}
					}
				});

				rb.setVisibility(View.VISIBLE);
			} else {
				if (item.getRating() > 0) {
					rb.setNumStars(item.getRating());
					rb.setRating(rb.getNumStars());
					rb.setVisibility(View.VISIBLE);
				} else
					rb.setVisibility(View.GONE);
			}
		}

		ImageButton ib = view.findViewById(R.id.ibMenu);
		if (ib != null) {
			ib.setTag(position);

			ib.setOnClickListener(popupMenu);
		}

		/*
		 * CheckBox cb = (CheckBox) view.findViewById(R.id.cbUpdatable); if (cb
		 * != null) { cb.setChecked(item.isUpdatable());
		 * cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
		 * {
		 *
		 * @Override public void onCheckedChanged(CompoundButton buttonView,
		 * boolean isChecked) { updateItem(item.setUpdatable(isChecked));
		 *
		 * } }); }
		 */
		ImageView iv = view.findViewById(R.id.ivAlbum);
		if (iv != null)
			if (item.isAlbum())
				iv.setVisibility(View.VISIBLE);
			else
				iv.setVisibility(View.INVISIBLE);

		TextView tv = view.findViewById(R.id.tvTitle);
		if (tv != null) {
			tv.setText(item.getTitle());
		}

		tv = view.findViewById(R.id.tvTitleAlt);
		if (tv != null) {
			tv.setText(item.getTitle());
		}

		tv = view.findViewById(R.id.tvAlbumTitle);
		if (tv != null) {
			if (item.isAlbum()) {
				tv.setText(item.getTitle());
				tv.setVisibility(View.VISIBLE);
			} else
				tv.setVisibility(View.INVISIBLE);
		}

		tv = view.findViewById(R.id.tvUpdated);
		if (tv != null) {
			tv.setText(context.getString(R.string.updated_value,
					item.getFormattedUpdateDate(Library.DATEFORMAT_ADAPTIVE)));
		}

		tv = view.findViewById(R.id.tvCreated);
		if (tv != null) {
			tv.setText(context.getString(R.string.created_value,
					item.getFormattedCreateDate(Library.DATEFORMAT_ADAPTIVE)));
		}

		tv = view.findViewById(R.id.tvDescription);
		if (tv != null) {
			tv.setText(item.getDescription());
		}

		tv = view.findViewById(R.id.tvLink);
		if (tv != null)
			tv.setText(item.getLink());

		tv = view.findViewById(R.id.tvTotalCounter);
		if (tv != null) {
			if (item.isAlbum() && (i = getEntries(new SelectionAlbum(item.getId())).getCount()) > 0) {
				// int i = getEntries(new
				// SelectionAlbum(item.getId())).getCount();
				// if (i > 0) {
				tv.setText(i + "");
				tv.setVisibility(View.VISIBLE);
			} else {
				tv.setVisibility(View.GONE);
			}
		}

		tv = view.findViewById(R.id.tvNewCounter);
		if (tv != null) {
			if ((i = getEntries(new SelectionNew(item.getId())).getCount()) > 0) {
				tv.setText(Html.fromHtml(context.getString(R.string.counter_new, i)));
				tv.setVisibility(View.VISIBLE);
			} else {
				tv.setVisibility(View.GONE);
			}
		}

		tv = view.findViewById(R.id.tvFailedCounter);
		if (tv != null) {
			if ((i = getEntries(new SelectionFault(item.getId())).getCount()) > 0) {
				tv.setText(Html.fromHtml(context.getString(R.string.counter_fails, i)));
				tv.setVisibility(View.VISIBLE);
			} else {
				tv.setVisibility(View.GONE);
			}
		}

		return view;
	}


	private void callPicasso(final Entity<?> item, final View view) {
		final ProgressBar progressBar = view.findViewById(R.id.pbThumb);
		final ImageView iv = view.findViewById(R.id.ivThumb);
		final ImageView ivNew = view.findViewById(R.id.ivNew);
		if (ivNew != null)
			ivNew.setVisibility(View.INVISIBLE);

		View l = view.findViewById(R.id.rlError);
		if (l != null)
			l.setVisibility(View.INVISIBLE);

		progressBar.setVisibility(View.VISIBLE);


		/*
		 * Target target = new Target() {
		 *
		 * @Override public void onBitmapLoaded(final Bitmap bitmap,
		 * Picasso.LoadedFrom from) { new Thread(new Runnable() {
		 *
		 * @Override public void run() { File file = item.getImageCachePath();
		 * new File(file.getAbsolutePath()).getParentFile().mkdirs();
		 * System.out.println("SAVETO=" + item.getImageCachePath().toString());
		 * try { file.createNewFile(); FileOutputStream ostream = new
		 * FileOutputStream(file); bitmap.compress(CompressFormat.JPEG, 75,
		 * ostream); ostream.close(); } catch (Exception e) {
		 * e.printStackTrace(); }
		 *
		 * } }).start(); }
		 *
		 * @Override public void onBitmapFailed(Drawable errorDrawable) { }
		 *
		 * @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
		 * if (placeHolderDrawable != null) { } } };
		 */
		if (isHeader()) {
			if (view instanceof ViewSwitcher) {
				final View myFirstView = view.findViewById(R.id.rlFirst);
				final View mySecondView = view.findViewById(R.id.rlSecond);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (((ViewSwitcher) view).getCurrentView() != myFirstView) {
							((ViewSwitcher) view).showPrevious();
						} else if (((ViewSwitcher) view).getCurrentView() != mySecondView) {
							((ViewSwitcher) view).showNext();
						}
					}
				});
			}
			if (iv != null) {
				progressBar.setVisibility(View.VISIBLE);

				int width = (int) context.getResources().getDimension(R.dimen.gallery_header_thumb_size);
				int height = (int) context.getResources().getDimension(R.dimen.gallery_header_thumb_size);

				Picasso.get().load(item.getImageCache())
						.resize(width, height)
						.error(R.drawable.ic_warning_black_48dp).centerCrop()
						.into(iv, new com.squareup.picasso.Callback() {
							@Override
							public void onSuccess() {
								progressBar.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onError(Exception e) {
								System.err.println(e);
								progressBar.setVisibility(View.INVISIBLE);
							}
						});

				if (!item.isImageCached())
					PictureLoader.saveImage(item.getImageLink(), item.getImageCachePath().getAbsolutePath());
				// Picasso.with(Main.getContext()).load(item.getImageCache()).into(target);
			}

		} else if (getActiveParent() > 0) {

			Picasso.get().load(item.getImageCache())
					.resize(getThumbOptimalSize(), getThumbOptimalSize()).centerCrop().noFade()
					.into(iv, new com.squareup.picasso.Callback() {
						@Override
						public void onSuccess() {
							progressBar.setVisibility(View.INVISIBLE);

							if (ivNew != null)
								ivNew.setVisibility(item.getAccessDate() != null ? View.INVISIBLE : View.VISIBLE);
							view.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (item.isAlbum()) {
										setSelectionTerms(new SelectionAlbum(item.getParentId()));

										Main.getMainActivity().onNavigationDrawerItemSelected(item.getParentId(),
												item.getId(), 0);
										// Fragment fragment = new
										// GalleryListFragment(item.getParentId(),
										// item.getId(),
										// 0);

										// if (fragment != null) {
										// FragmentManager fragmentManager =
										// context.get.getFragmentManager();
										// fragmentManager.beginTransaction().replace(R.id.container,
										// fragment)
										// .addToBackStack(null).commit();
										// }

										updateItemValue(item.getId(), ItemAdapter.KEY_ACCESS_DATE, new Date());
										// System.out.println(
										// "SETVALUES=" + item.getParentId() +
										// ":" + item.getId() + ":" + 0);
									} else {
										Intent intent = new Intent(context, PictureActivity.class);
										intent.putExtra(PictureActivity.CURRENT_GALLERY_ID, item.getParentId());
										intent.putExtra(PictureActivity.CURRENT_PICTURE_ID, item.getId());
										context.startActivity(intent);// , 300);
									}
								}
							});

							if (item.isFailed()) {
								updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_FAILED, false);
								// refresh();
							}

						}

						@Override
						public void onError(Exception e) {
							System.err.println(e);

							view.findViewById(R.id.rlError).setVisibility(View.VISIBLE);
							TextView tv = view.findViewById(R.id.tvError);
							if (tv != null) {
								if (NetworkState.getInstance().isAvailable()) {
									tv.setText(context.getString(R.string.error));
								} else {
									tv.setText(context.getString(R.string.network_error));
								}
								tv.setVisibility(View.VISIBLE);
							}

							if (view instanceof ViewSwitcher) {
								final View myFirstView = view.findViewById(R.id.rlThumbFirst);
								final View mySecondView = view.findViewById(R.id.rlThumbSecond);
								view.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										if (((ViewSwitcher) view).getCurrentView() != myFirstView) {
											((ViewSwitcher) view).showPrevious();
										} else if (((ViewSwitcher) view).getCurrentView() != mySecondView) {
											((ViewSwitcher) view).showNext();
										}
									}
								});
							}

							progressBar.setVisibility(View.INVISIBLE);

							ImageView ivNew = view.findViewById(R.id.ivNew);
							if (ivNew != null)
								ivNew.setVisibility(View.INVISIBLE);

							item.getImageCachePath().delete();

							if (!item.isFailed() && NetworkState.getInstance().isAvailable()) {
								updateItemValueNoRefresh(item.getId(), ItemAdapter.KEY_FAILED, true);
								// refresh();
							}

						}
					});

			if (!item.isImageCached()) {
				PictureLoader.saveImage(item.getImageLink(), item.getImageCachePath().getAbsolutePath());
				// Picasso.with(Main.getContext()).load(item.getImageCache()).into(target);
			}

		} else {
			// iv = (ImageView) view.findViewById(R.id.ivThumb);

			switch (Library.getThumbType(item)) {
			case 0:

				int height = (int) context.getResources().getDimension(R.dimen.library_header_thumb_height);
				int width = (int) context.getResources().getDimension(R.dimen.library_header_thumb_width);
				if (width == 0)
					width = ItemAdapter.getBigOptimalWidth();

				Picasso.get().load(item.getImageCache()).resize(width, height)
						.error(R.drawable.ic_warning_black_48dp).centerCrop()
						.into(iv, new com.squareup.picasso.Callback() {
							@Override
							public void onSuccess() {
								progressBar.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onError(Exception e) {
								System.err.println(e);
								progressBar.setVisibility(View.INVISIBLE);

							}
						});

				if (!item.isImageCached())
					PictureLoader.saveImage(item.getImageLink(), item.getImageCachePath().getAbsolutePath());
				// Picasso.with(Main.getContext()).load(item.getImageCache()).into(target);

				// }
				break;
			case 1:
				Picasso.get().load(R.drawable.icon_image_grey).into(iv);
				progressBar.setVisibility(View.INVISIBLE);
				break;
			case 2:
				// iv.setRotation(180);
				break;
			}
		}
	}

	View.OnClickListener popupMenu = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			PopupMenu popup = new PopupMenu(context, v);
			if (isHeader()) {
				popup.getMenuInflater().inflate(R.menu.item_gallery_popup, popup.getMenu());
				if (!isFirstLevelItem(getItem(Integer.valueOf(v.getTag().toString())))) {
					popup.getMenu().findItem(R.id.clear).setVisible(false);
					popup.getMenu().findItem(R.id.hide).setVisible(false);
				}
			} else if (getActiveParent() > 0) {
				popup.getMenuInflater().inflate(R.menu.item_picture_popup, popup.getMenu());
			} else {
				popup.getMenuInflater().inflate(R.menu.item_library_popup, popup.getMenu());
			}

			if (isFirstLevelItem(getItem(Integer.valueOf(v.getTag().toString())))) {
				popup.getMenu().findItem(R.id.remove).setVisible(false);
			}

			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem menuItem) {
					final Item item = (Item) getItem(Integer.valueOf(v.getTag().toString()));

					switch (menuItem.getItemId()) {
					case R.id.update:
						View view = ((View) v.getParentForAccessibility());
						if (isHeader())
							getInstance(context, item.getId()).refresh();
						else
							updateItemValueNoRefresh(item.getId(), KEY_UPDATE_DATE, new Date());
						callPicasso(item, view);
						break;
					case R.id.remove:
						if (isHeader() && !isFirstLevelItem(item)) {
							DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (which == AlertDialog.BUTTON_POSITIVE) {
										new ClearChildrenTask(item, false).execute();
										// updateItemValue(item.getParentId(),
										// KEY_UPDATE_DATE, new Date());

										ItemAdapter la = getInstance(context, item.getParentId());
										// System.out.println("ITEM=" +
										// la.getItem(la.getPosition()).toString());

										Main.getMainActivity().onNavigationDrawerItemSelected(item.getParentId(),
												la.getNextItemId(la.getPosition()), 0);
										dismissItem(item);
									}
								}
							};

							AlertDialog.Builder alert = new AlertDialog.Builder(context);
							AlertDialog dialog = alert.setCancelable(false).setMessage(R.string.album_will_be_removed)
									.setTitle(R.string.alert).setPositiveButton(android.R.string.yes, listener)
									.setNegativeButton(android.R.string.cancel, listener).setCancelable(false).create();
							dialog.show();
						} else if (!isFirstLevelItem(item) && item.getParentId() > 0) {
							if (item.isAlbum()) {
								new ClearChildrenTask(item, false).execute();
							}
							// System.out.println("AAA");
							updateItemValueNoRefresh(item.getParentId(), KEY_UPDATE_DATE, new Date());
							dismissItem(item);
							// ItemAdapter.getInstance(item.getParentId()).notifyDataSetChanged();//
							// .refresh();
							// ItemAdapter la = getInstance(item.getParentId());
							// Main.getMainActivity().onNavigationDrawerItemSelected(item.getParentId(),
							// la.getItemId(la.getPosition()), 0);

						} else if (item.getParentId() == 0) {
							DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (which == AlertDialog.BUTTON_POSITIVE) {
										new RemoveChildrenTask(item).execute();
										removeItem(item);
									}
								}
							};

							AlertDialog.Builder alert = new AlertDialog.Builder(context);
							AlertDialog dialog = alert.setCancelable(false)
									.setMessage(R.string.library_will_be_removed_entirely).setTitle(R.string.alert)
									.setPositiveButton(android.R.string.yes, listener)
									.setNegativeButton(android.R.string.cancel, listener).setCancelable(false).create();
							dialog.show();

						}
						break;
					case R.id.clear: // clear from header or library
						DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which == AlertDialog.BUTTON_POSITIVE) {
									new ClearChildrenTask(item, true).execute();
									// ItemAdapter la =
									// ItemAdapter.getInstance(item.getParentId());
									Main.getMainActivity().onNavigationDrawerItemSelected(item.getParentId(),
											item.getId(), 0);
								}
							}
						};

						AlertDialog.Builder alert = new AlertDialog.Builder(context);
						AlertDialog dialog = alert.setCancelable(false).setMessage(R.string.gallery_will_be_cleared)
								.setTitle(R.string.alert).setPositiveButton(android.R.string.yes, listener)
								.setNegativeButton(android.R.string.cancel, listener).setCancelable(false).create();
						dialog.show();
						break;
					case R.id.open_in_web:
						if (item.getLink().length() > 0) {
							context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()))
									.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						} else {
							Toast.makeText(context, R.string.not_found_link_for_this_item, Toast.LENGTH_SHORT)
									.show();
						}
						break;
					case R.id.thumbnail_type_default:
						Library.setThumbType(item, 0);
						notifyDataSetChanged();
						break;
					case R.id.thumbnail_type_mask:
						Library.setThumbType(item, 1);
						notifyDataSetChanged();
						break;
					// case R.id.thumbnail_type_collage:
					// Library.setThumbType(item, 2);
					// notifyDataSetChanged();
					// break;
					case R.id.all_shows:
						updateAllItemsValueIfEmpty(item.getId(), KEY_ACCESS_DATE, new Date());
						getInstance(context, item.getId()).refresh();
						// notifyDataSetChanged();
						break;
					case R.id.options:
						Main.getMainActivity().onCallback("options", (int) item.getId());
						break;
					case R.id.hide:
						listener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								item.setVisible(false);
								if (which == AlertDialog.BUTTON_POSITIVE)
									item.setUpdatable(false);

								updateItem(item);

								ItemAdapter la = getInstance(context, item.getParentId());
								Main.getMainActivity().onNavigationDrawerItemSelected(item.getParentId(),
										la.getItemId(la.getPosition()), 0);
							}
						};

						alert = new AlertDialog.Builder(context);
						dialog = alert.setCancelable(false)
								.setMessage(R.string.gallery_will_be_invisible_but_you_can_restore_it)
								.setTitle(R.string.alert).setPositiveButton(android.R.string.yes, listener)
								.setNegativeButton(R.string.no, listener).setCancelable(false).create();
						dialog.show();

						break;
					case R.id.remove_faulty:
						removeFault(item.getId());

						// View parentView = ((View)
						// v.getParentForAccessibility());
						// TextView tv = (TextView)
						// parentView.findViewById(R.id.tvFailedCounter);
						// if (tv != null) {
						// int i =
						// pa.getAllFailedEntries(gal.getId()).getCount();
						// if (i > 0) {
						// tv.setText(Html.fromHtml(context.getString(R.string.counter_fails,
						// i)));
						// tv.setVisibility(View.VISIBLE);
						// } else {
						// tv.setVisibility(View.GONE);
						// }
						// tv = (TextView)
						// parentView.findViewById(R.id.tvTotalCounter);
						// if (tv != null) {
						// tv.setText(pa.getCount() + "");
						// }
						// }
						// tv = (TextView)
						// parentView.findViewById(R.id.tvTotalCounter);
						// if (tv != null) {
						// tv.setText(pa.getCount() + "");
						// }
						break;
					}
					return true;
				}
			});

			popup.show();// showing popup menu
		}
	};

	class ClearChildrenTask extends AsyncTask<Integer, Void, String> {
		Item item;
		boolean showProgress;

		public ClearChildrenTask(Item item, boolean showProgress) {
			this.item = item;
			this.showProgress = showProgress;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showProgress)
				Main.getMainActivity().showProgressDialog();
		}

		@Override
		protected String doInBackground(Integer... params) {

			updateItemValueNoRefresh(item.getParentId(), KEY_UPDATE_DATE, new Date());

			Cursor cur = getEntries(new SelectionAlbum(item.getId()));
			if (cur.moveToFirst()) {
				while (!cur.isAfterLast()) {
					dismissItemRecursively(getItem(cur));
					// updateItemNoRefresh(getItem(cur));
					cur.moveToNext();
				}
			}
			cur.close();

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (showProgress) {
				Main.getMainActivity().dismissProgressDialog();
			}
			getInstance(context, item.getId()).refresh();
		}
	}

	class RemoveChildrenTask extends AsyncTask<Integer, Void, String> {
		Item item;

		public RemoveChildrenTask(Item item) {
			this.item = item;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Main.getMainActivity().showProgressDialog();
		}

		@Override
		protected String doInBackground(Integer... params) {
			String name = item.getName();
			removeItemRecursively(item);
			Library.clearPreferences(name);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			getInstance(context, item.getId()).refresh();

		}

	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	public long addItem(Entity<?> item) {
		ContentValues values = new ContentValues();
		// values.put(KEY_ALBUM, item.isAlbum() ? 1 : 0);
		// values.put(KEY_ALBUM_ID, item.getAlbumId());
		return addItem(item, values);
	}

	public long addItem(Item item) {
		ContentValues values = new ContentValues();
		// values.put(KEY_ALBUM, item.isAlbum() ? 1 : 0);
		// values.put(KEY_ALBUM_ID, item.getAlbumId());
		return addItem(item, values);
	}

	@Override
	public boolean updateItem(Entity<?> item) {
		ContentValues values = new ContentValues();
		// values.put(KEY_FAILED, item.isFailed() ? 1 : 0);
		// values.put(KEY_ALBUM, item.isAlbum() ? 1 : 0);
		// values.put(KEY_ALBUM_ID, item.getAlbumId());
		return updateItem(item, values);
	}

	@Override
	public boolean updateItemNoRefresh(Entity<?> item) {
		ContentValues values = new ContentValues();
		// values.put(KEY_FAILED, item.isFailed() ? 1 : 0);
		// values.put(KEY_ALBUM, item.isAlbum() ? 1 : 0);
		// values.put(KEY_ALBUM_ID, item.getAlbumId());
		return updateItemNoRefresh(item, values);
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

}
