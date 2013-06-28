package com.alidi;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.alidi.pojos.KPIBaseItem;
import com.alidi.util.FileUtil;
import com.alidi.util.IOUtil;
import com.alidi.util.Message;
import com.alidi.util.Strings;
import com.example.KPI.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class KPIActivity extends BaseActivity {
    public static final String BASE_RELATIVE_URL = "http://81.18.138.11:45535/Strvoitov/KPISerlet";
    public static final String KPI = "KPI";
    public static final String KPI_DIMENSION_DESCR = " млн руб";
    ExpandableListView v;
    ExpandableListAdapter adapter;
    ArrayList<KPIBaseItem> kpiList;
    private Activity activity;
    private Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = this;
        context = this;
        addOptionsMenuHackerInflaterFactory();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.kpi);
        v = (ExpandableListView) findViewById(R.id.expandableListView);
        File blReg = new File(Common.KPI_DB);
        initDatabase();
        initKPIList();
        initAdapter();
        v.setAdapter(adapter);
    }

    private void initDatabase() {
        IOUtil.checkAndCreatePath(Common.CASH_PATH);
        if (!new File(Common.KPI_DB).exists()) {
            InputStream inputStream = getResources().openRawResource(R.raw.kpi_data);
            try {
                File kpiDb = new File(Common.KPI_DB);
                kpiDb.createNewFile();
                FileUtil.copyFile(inputStream, new FileOutputStream(kpiDb));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initKPIList() {
        SQLiteDatabase db;

        db = openOrCreateDatabase(
                Common.KPI_DB
                , SQLiteDatabase.CREATE_IF_NECESSARY
                , null
        );
        String select = "SELECT isd.TBL_ID as A, TBL_DESC,isd.COL_ID,COL_DESC,isd.ROW_ID,ROW_DESC, VAL , DT\n" +
                "  FROM ImpConsolidated_Data AS isd\n" +
                "       INNER JOIN ImpConsolidated_TBLS AS ist\n" +
                "               ON isd.TBL_ID = ist.TBL_ID\n" +
                "       INNER JOIN ImpConsolidated_COLS AS isc\n" +
                "               ON isd.COL_ID = isc.COL_ID\n" +
                "       INNER JOIN ImpConsolidated_ROWS AS isr\n" +
                "               ON isd.ROW_ID = isr.ROW_ID\n" +
                "  WHERE isd.COL_ID = '50' AND isd.ROW_ID = '500' ";
        kpiList = new ArrayList<KPIBaseItem>();
        Cursor cursorParent = db.rawQuery(select, null);
        if (cursorParent.moveToFirst()) {
            try {
                do {
                    String name = cursorParent.getString(cursorParent.getColumnIndex("TBL_DESC"));
                    Integer id = Integer.valueOf(cursorParent.getString(cursorParent.getColumnIndex("A")));
                    String value = cursorParent.getString(cursorParent.getColumnIndex("VAL"));
                    String tblSelect = "SELECT DISTINCT isd.TBL_ID as A, TBL_DESC,isd.COL_ID,COL_DESC,isd.ROW_ID,ROW_DESC, VAL , DT\n" +
                            "  FROM ImpConsolidated_Data AS isd\n" +
                            "       INNER JOIN ImpConsolidated_TBLS AS ist\n" +
                            "               ON isd.TBL_ID = ist.TBL_ID\n" +
                            "       INNER JOIN ImpConsolidated_COLS AS isc\n" +
                            "               ON isd.COL_ID = isc.COL_ID\n" +
                            "       INNER JOIN ImpConsolidated_ROWS AS isr\n" +
                            "               ON isd.ROW_ID = isr.ROW_ID\n" +
                            "  WHERE isd.TBL_ID = '" + id.toString() + "' and isd.COL_ID = '50'  ";

                    Cursor cursorCh = db.rawQuery(tblSelect, null);
                    ArrayList<KPIBaseItem> childs = new ArrayList<KPIBaseItem>(cursorCh.getCount());
                    if (cursorCh.moveToFirst()) {
                        try {
                            do {
                                String nameCh = cursorCh.getString(cursorCh.getColumnIndex("ROW_DESC"));
                                Integer idCh = Integer.valueOf(cursorCh.getString(cursorCh.getColumnIndex("A")));
                                String valueCh = cursorCh.getString(cursorCh.getColumnIndex("VAL"));
                               // Integer groupId = cursorCh.getInt(cursorCh.getColumnIndex("GROUP_ID"));
                                KPIBaseItem itemCh = new KPIBaseItem(null, idCh, nameCh, valueCh);
                                childs.add(itemCh);

                            } while (cursorCh.moveToNext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            cursorCh.close();
                        }
                    }
                    KPIBaseItem itemAdd = null;
                   /* String cSelect = "SELECT count FROM ";
                    Cursor cursorC = db.rawQuery(cSelect, null);
                    int count = cursorC.getInt(0);
                    if (count > 1) {
                        String addSelect = "";
                        Cursor cursorAd = db.rawQuery(addSelect, null);
                        if (cursorAd.moveToFirst()) {
                            try {
                                do {
                                    String nameCh = cursorCh.getString(cursorCh.getColumnIndex("ROW_DESC"));
                                    Integer idCh = Integer.valueOf(cursorCh.getString(cursorCh.getColumnIndex("A")));
                                    String valueCh = cursorCh.getString(cursorCh.getColumnIndex("VAL"));
                                    itemAdd = new KPIBaseItem(null, idCh, nameCh, valueCh);
                                } while (cursorCh.moveToNext());
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                cursorCh.close();
                            }
                        }
                    }*/
                    KPIBaseItem item = new KPIBaseItem(childs, id, name, value);
                    item.setAdditionalParam(itemAdd);
                    kpiList.add(item);
                } while (cursorParent.moveToNext());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursorParent.close();
                db.close();

            }
        }


      /*  kpiList = new ArrayList<KPIBaseItem>();
        ArrayList<KPIBaseItem> first = new ArrayList<KPIBaseItem>();
        first.add(new KPIBaseItem(first, "Продажи PG:", "2052107182.38501"));
        first.add(new KPIBaseItem(first, "Продажи Nestle:", "2052107182.38501"));
        kpiList.add(new KPIBaseItem(first, "Продажи по тек.число, руб с НДС:", "2052107182.38501"));
        kpiList.add(new KPIBaseItem(null, "Трэкинг, руб с НДС:", "323"));
*/

    }

    private void initAdapter() {
        adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return kpiList.size();
            }

            @Override
            public int getChildrenCount(int i) {
                KPIBaseItem item = kpiList.get(i);
                ArrayList<KPIBaseItem> children = null;
                if (item != null) {
                    children = item.children;
                }
                if (children != null)
                    return children.size();
                else return 0;
            }

            @Override
            public Object getGroup(int i) {
                return kpiList.get(i);
            }

            @Override
            public Object getChild(int i, int i2) {
                KPIBaseItem kpiBaseItem = (KPIBaseItem) kpiList.get(i);
                return kpiBaseItem.children.get(i2);
            }

            @Override
            public long getGroupId(int i) {
                return i;
            }

            @Override
            public long getChildId(int i, int i2) {
                return i2;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.child_view, null, false);
          /*  RatingBar ratingBar = (RatingBar) linearLayout.findViewById(R.id.ratingBar);
            ratingBar.setEnabled(false);
            ratingBar.setRating(3);*/
                KPIBaseItem group = (KPIBaseItem) getGroup(i);
                LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(0);

                LinearLayout linearLayoutUp = (LinearLayout) linearLayout1.getChildAt(0);
                LinearLayout linearLayoutDown = (LinearLayout) linearLayout1.getChildAt(1);
                try {

                    TextView name = (TextView) linearLayoutUp.getChildAt(0);
                    name.setText(group.getName());

                    TextView val = (TextView) linearLayoutUp.getChildAt(1);
                    String value = group.getValue();
                    int intValue = 0;
                    if (IOUtil.tryToParseDouble(value))
                        intValue = (int) Double.parseDouble(value);
                    val.setText(intValue + KPI_DIMENSION_DESCR);

                 /*   KPIBaseItem additionalParam = group.getAdditionalParam();

                    TextView nameAd = (TextView) linearLayoutDown.getChildAt(0);
                    nameAd.setText(additionalParam.getName());

                    TextView valAd = (TextView) linearLayoutDown.getChildAt(1);
                    String valueAd = additionalParam.getValue();
                    int intValueAd = 0;
                    if (IOUtil.tryToParseDouble(valueAd))
                        intValueAd = (int) Double.parseDouble(valueAd);
                    valAd.setText(intValueAd + KPI_DIMENSION_DESCR);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return linearLayout;
            }

            @Override
            public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.child_view, viewGroup, false);
                LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(0);

                LinearLayout linearLayoutUp = (LinearLayout) linearLayout1.getChildAt(0);
                LinearLayout linearLayoutDown = (LinearLayout) linearLayout1.getChildAt(1);

                //TextView name = (TextView) linearLayout.findViewById(R.id.textView);
                TextView name = (TextView) linearLayoutUp.getChildAt(0);
                name.setText(((KPIBaseItem) getChild(i, i2)).getName());

                //TextView val = (TextView) linearLayout.findViewById(R.id.textView1);
                TextView val = (TextView) linearLayoutUp.getChildAt(1);
                String value = ((KPIBaseItem) getChild(i, i2)).getValue();
                int intValue = 0;
                if (IOUtil.tryToParseDouble(value))
                    intValue = (int) Double.parseDouble(value);
                val.setText(intValue + KPI_DIMENSION_DESCR);

                linearLayout.setPadding(40, 0, 0, 0);

                linearLayout.setTag(i + "_" + i2);
                /*linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String s = view.getTag().toString();
                        String regularExpression = "_";
                        String[] strings = s.split(regularExpression);


                    }
                });*/
                return linearLayout;
            }

            @Override
            public boolean isChildSelectable(int i, int i2) {
                return true;
            }
        };
    }

    public boolean updateKPI() {
        File file = new File(Common.JSON_KPI);
        String relativeUrl = BASE_RELATIVE_URL;
        boolean response = CommonApiFeatures.readJSONtoFile(relativeUrl, file);
        String json = "";
        json = CommonApiFeatures.makeJSONStringWithAssetsFromFile(file);
        try {
            JSONObject ob = new JSONObject(json);
            JSONArray baseKPIJSON = ob.getJSONArray(KPI);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(0, 0, 0, "");
      /*  MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);*/
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0: {
                final Monitor serviceStartupMonitor = new Monitor().once();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean[] b = new boolean[1];
                        b[0] = false;
                        startWaiting();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                serviceStartupMonitor.doNotify();
                            }
                        }).start();
                        serviceStartupMonitor.doWait();
                        activity.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        cancelWaiting();


                                    }
                                }
                        );
                    }
                }).start();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    static Class IconMenuItemView_class = null;
    @SuppressWarnings("rawtypes")
    static Constructor IconMenuItemView_constructor = null;

    // standard signature of constructor expected by inflater of all View classes
    @SuppressWarnings("rawtypes")
    private static final Class[] standard_inflater_constructor_signature =
            new Class[]{Context.class, AttributeSet.class};

    protected void addOptionsMenuHackerInflaterFactory() {
        final LayoutInflater infl = getLayoutInflater();

        infl.setFactory(new LayoutInflater.Factory() {
            public View onCreateView(final String name,
                                     final Context context,
                                     final AttributeSet attrs) {
                if (!name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView"))
                    return null; // use normal inflater

                View view;
                 /* = new ImageButton(context);
                view.setImageResource(R.drawable.appwidget_bg4_1);
                return  view;*/
                // "com.android.internal.view.menu.IconMenuItemView"
                // - is the name of an internal Java class
                //   - that exists in Android <= 3.2 and possibly beyond
                //   - that may or may not exist in other Android revs
                // - is the class whose instance we want to modify to set background etc.
                // - is the class we want to instantiate with the standard constructor:
                //     IconMenuItemView(context, attrs)
                // - this is what the LayoutInflater does if we return null
                // - unfortunately we cannot just call:
                //     infl.createView(name, null, attrs);
                //   here because on Android 3.2 (and possibly later):
                //   1. createView() can only be called inside inflate(),
                //      because inflate() sets the context parameter ultimately
                //      passed to the IconMenuItemView constructor's first arg,
                //      storing it in a LayoutInflater instance variable.
                //   2. we are inside inflate(),
                //   3. BUT from a different instance of LayoutInflater (not infl)
                //   4. there is no way to get access to the actual instance being used
                // - so we must do what createView() would have done for us
                //
                if (IconMenuItemView_class == null) {
                    try {
                        IconMenuItemView_class = getClassLoader().loadClass(name);
                    } catch (ClassNotFoundException e) {
                        // this OS does not have IconMenuItemView - fail gracefully
                        return null; // hack failed: use normal inflater
                    }
                }
                if (IconMenuItemView_class == null)
                    return null; // hack failed: use normal inflater

                if (IconMenuItemView_constructor == null) {
                    try {
                        IconMenuItemView_constructor =
                                IconMenuItemView_class.getConstructor(standard_inflater_constructor_signature);
                    } catch (SecurityException e) {
                        return null; // hack failed: use normal inflater
                    } catch (NoSuchMethodException e) {
                        return null; // hack failed: use normal inflater
                    }
                }
                if (IconMenuItemView_constructor == null)
                    return null; // hack failed: use normal inflater

                try {
                    Object[] args = new Object[]{context, attrs};
                    view = (View) (IconMenuItemView_constructor.newInstance(args));


                } catch (IllegalArgumentException e) {
                    return null; // hack failed: use normal inflater
                } catch (InstantiationException e) {
                    return null; // hack failed: use normal inflater
                } catch (IllegalAccessException e) {
                    return null; // hack failed: use normal inflater
                } catch (InvocationTargetException e) {
                    return null; // hack failed: use normal inflater
                }
                if (null == view) // in theory handled above, but be safe...
                    return null; // hack failed: use normal inflater


                // apply our own View settings after we get back to runloop
                // - android will overwrite almost any setting we make now
                final View v = view;
                new Handler().post(new Runnable() {
                    public void run() {

                        v.setBackgroundResource(R.drawable.fitst_bg_view);
                    /*    try
                        {
                            // in Android <= 3.2, IconMenuItemView implemented with TextView
                            // guard against possible future change in implementation
                            TextView tv = (TextView)v;
                            tv.setTextColor(Color.WHITE);
                            tv.setTextSize(50);
                            //tv.setBackgroundResource(R.drawable.appwidget_bg4_1);
                            tv.setAlpha(50);


                        }
                        catch (ClassCastException e)
                        {
                            // hack failed: do not set TextView attributes
                        }*/
                    }
                });

                return view;
            }
        });
    }

    public class ParentLevel extends BaseExpandableListAdapter {


        @Override
        public Object getChild(int i, int i2) {
            KPIBaseItem kpiBaseItem = (KPIBaseItem) kpiList.get(i);
            return kpiBaseItem.children.get(i2);
        }

        @Override
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            CustExpListview SecondLevelexplv = new CustExpListview(context, groupPosition);
            SecondLevelexplv.setAdapter(new SecondLevelAdapter(groupPosition));
            SecondLevelexplv.setGroupIndicator(null);
            return SecondLevelexplv;
        }

        @Override
        public int getGroupCount() {
            return kpiList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return kpiList.get(i).children.size();
        }

        @Override
        public Object getGroup(int i) {
            return kpiList.get(i);
        }


        @Override
        public long getGroupId(int i) {
            return i;
        }


        @Override
        public View getGroupView(int i, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.child_view, null);
           /* RatingBar ratingBar = (RatingBar) linearLayout.findViewById(R.id.ratingBar);
            ratingBar.setEnabled(false);
            ratingBar.setRating(2);*/
            TextView name = (TextView) linearLayout.findViewById(R.id.textView);
            name.setText(((KPIBaseItem) getGroup(i)).getName());
            TextView val = (TextView) linearLayout.findViewById(R.id.textView1);
            val.setText(((KPIBaseItem) getGroup(i)).getValue());
            return linearLayout;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class CustExpListview extends ExpandableListView {

        int intGroupPosition, intChildPosition, intGroupid;

        public CustExpListview(Context context, int intGroupPosition) {
            super(context);
            this.intGroupPosition = intGroupPosition;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public class SecondLevelAdapter extends BaseExpandableListAdapter {
        int i;

        public SecondLevelAdapter(int i) {
            super();
            this.i = i;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv = new TextView(context);
            tv.setText("child");
            tv.setPadding(15, 5, 5, 5);
            tv.setBackgroundColor(Color.YELLOW);
            tv.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            return tv;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 5;
        }

        @Override
        public Object getGroup(int groupPosition) {
            KPIBaseItem kpiBaseItem = kpiList.get(i);
            ArrayList<KPIBaseItem> children = kpiBaseItem.children;
            return children.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return kpiList.get(i).children.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int i2, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.child_view, null, false);
          /*  RatingBar ratingBar = (RatingBar) linearLayout.findViewById(R.id.ratingBar);
            ratingBar.setEnabled(false);
            ratingBar.setRating(3);*/
            TextView name = (TextView) linearLayout.findViewById(R.id.textView);
            name.setText(((KPIBaseItem) getGroup(i2)).getName());
            TextView val = (TextView) linearLayout.findViewById(R.id.textView1);
            String value = ((KPIBaseItem) getGroup(i2)).getValue();
            int intValue = 0;
            if (IOUtil.tryToParse(value))
                intValue = Integer.parseInt(value);
            val.setText(intValue + "руб");
            return linearLayout;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

    }
}