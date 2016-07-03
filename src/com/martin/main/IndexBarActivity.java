package com.martin.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.martin.beans.FriendBean;
import com.martin.view.IndexBar;
import com.martin.view.IndexBar.OnIndexListener;
import com.martin.view.R;

public class IndexBarActivity extends Activity {
	IndexBar indexBar;
	ListView listView;
	TextView tvIndex;
	List<FriendBean> friends = new ArrayList<FriendBean>();

	private FriendAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_indexbar);
		indexBar = (IndexBar) findViewById(R.id.indexBar);
		listView = (ListView) findViewById(R.id.listView);
		tvIndex = (TextView) findViewById(R.id.tvIndex);
		initData();
		setListener();
		listView.setAdapter(adapter = new FriendAdapter(this, friends));
		System.out.println(adapter.getCount());
		System.out.println((adapter.getItem(4)).toString());
	}

	private void initData() {
		friends.add(new FriendBean("阿  *里   山"));
		friends.add(new FriendBean("马  云"));
		friends.add(new FriendBean("李彦宏"));
		friends.add(new FriendBean("罗永浩"));
		friends.add(new FriendBean("李永杰"));
		friends.add(new FriendBean("韩帮蜜"));
		friends.add(new FriendBean("倪红云"));
		friends.add(new FriendBean("占起航"));
		friends.add(new FriendBean("李春帅"));
		friends.add(new FriendBean("撒贝宁"));
		friends.add(new FriendBean("刘德华"));
		friends.add(new FriendBean("梁朝伟"));
		friends.add(new FriendBean("刘亦菲"));
		friends.add(new FriendBean("薛之谦"));
		friends.add(new FriendBean("刘涛"));
		friends.add(new FriendBean("胡歌"));
		friends.add(new FriendBean("月夜枫"));
		friends.add(new FriendBean("LongDD"));
		friends.add(new FriendBean("花样年华"));
		friends.add(new FriendBean("123456"));
		friends.add(new FriendBean("￥……*&"));

		// 排序
		Collections.sort(friends);
	}

	private void setListener() {
		indexBar.setOnIndexListener(new OnIndexListener() {

			@Override
			public void index(String index) {
				for (int i = 0; i < friends.size(); i++) {
					tvIndex.setVisibility(View.VISIBLE);
					tvIndex.setText(index.charAt(0) + "");

					FriendBean bean = friends.get(i);
					if (bean.nickPinYin.charAt(0) == index.charAt(0)) {
						listView.setSelection(i);
						break;
					}
				}
			}

			@Override
			public void cancle() {
				tvIndex.setVisibility(View.GONE);
			}
		});
	}

	class FriendAdapter extends BaseAdapter {

		Context context;
		List<FriendBean> friends;
		String currentCatalog;
		String lastCatalog;

		public FriendAdapter(Context context, List<FriendBean> friends) {
			this.context = context;
			this.friends = friends;
		}

		@Override
		public int getCount() {
			return friends.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_contact, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			FriendBean friendBean = friends.get(position);

			// friendBean.nickPinYin.charAt(0) 返回的是char
			// setText()方法会当成int，然后发生资源未找到的异常
			currentCatalog = friendBean.nickPinYin.charAt(0) + "";

			if (position > 0) {
				lastCatalog = friends.get(position - 1).nickPinYin.charAt(0) + "";
				if (currentCatalog.equals(lastCatalog)) {
					holder.catalog.setVisibility(View.GONE);
				} else {
					holder.catalog.setVisibility(View.VISIBLE);
					holder.catalog.setText(currentCatalog);
				}
			} else {
				holder.catalog.setVisibility(View.VISIBLE);
				holder.catalog.setText(currentCatalog);
			}

			holder.nickName.setText(friendBean.nickName);

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return friends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			TextView nickName;
			TextView catalog;

			public ViewHolder(View view) {
				nickName = (TextView) view.findViewById(R.id.contactitem_nick);
				catalog = (TextView) view.findViewById(R.id.contactitem_catalog);
			}

		}

	}
}
