package android_serialport_api.xingbang.firingdevice;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import android_serialport_api.xingbang.BaseActivity;
import android_serialport_api.xingbang.R;
import android_serialport_api.xingbang.db.Defactory;
import android_serialport_api.xingbang.db.GreenDaoMaster;
import android_serialport_api.xingbang.services.MyLoad;
import android_serialport_api.xingbang.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android_serialport_api.xingbang.Application.getDaoSession;

/**
 * @author zenghp
 *         厂家管理
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetFactoryActivity extends BaseActivity implements LoaderCallbacks<Cursor> {

    public static final Uri uri = Uri.parse("content://android_serialport_api.xingbang.Defactory");
    @BindView(R.id.setFactory_Name)
    EditText et_factory_name;
    @BindView(R.id.setFactory_Code)
    EditText et_factory_code;
    @BindView(R.id.setFactory_Feature)
    EditText et_factory_feature;
    @BindView(R.id.setFactory_isSelected)
    CheckBox et_factory_selected;
    @BindView(R.id.btn_factory_return)
    Button btn_return;
    @BindView(R.id.btn_factory_inputOk)
    Button btn_inputOk;
    @BindView(R.id.setFactoryBasePage)
    LinearLayout setFactoryBasePage;
    @BindView(R.id.factory_listView)
    ListView reisterListView;
    @BindView(R.id.container)
    LinearLayout container;
    private SimpleCursorAdapter adapter;
    private String TAG = "设置厂家";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_factory_page);
        ButterKnife.bind(this);

        // 标题栏
        setSupportActionBar(findViewById(R.id.toolbar));
        ListView factory_listView = (ListView) this.findViewById(R.id.factory_listView);
        adapter = new SimpleCursorAdapter(SetFactoryActivity.this, R.layout.factoryitem,
                null, new String[]{"deName", "deEntCode", "deFeatureCode", "isSelected"}, new int[]{R.id.deName, R.id.deEntCode, R.id.deFeatureCode, R.id.isSelected},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        factory_listView.setAdapter(adapter);

        factory_listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                ListView lr = (ListView) v;
                LinearLayout myte = (LinearLayout) lr.getChildAt(0);
                TextView dd = (TextView) myte.getChildAt(1);
                menu.setHeaderIcon(R.drawable.icon);
                menu.setHeaderTitle(dd.getText().toString());
                menu.add(0, 1, 3, getString(R.string.xingbang_main_page_btn_del));// "删除"
                menu.add(0, 2, 2, getString(R.string.text_tip_modify));//"修改"
                //设置第三个参数反向  所以出现的菜单是反着的
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO Auto-generated method stub
        args = new Bundle();
        args.putString("key", "2");
        MyLoad myLoad = new MyLoad(SetFactoryActivity.this, args);
        return myLoad;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void displayInputKeyboard(View v, boolean hasFocus) {
        //获取系统 IMM
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!hasFocus) {
            //隐藏 软键盘  
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            //显示 软键盘  
            imm.showSoftInput(v, 0);
        }
    }

    public void hideInputKeyboard() {

        et_factory_name.clearFocus();//取消焦点
        et_factory_code.clearFocus();
        et_factory_feature.clearFocus();//取消焦点
        et_factory_selected.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /***
     * 保存
     */
    private boolean insertFactory(String name, String code, String te, String selected) {
        if (checkRepeatCode(code)) {
            show_Toast(getString(R.string.text_fac_factory_name) + name + getString(R.string.text_fac_tip4));
            return false;
        }
        if (selected.equals(getString(R.string.text_setFac_yes)) && checkRepeatSelected(selected)) {
//            show_Toast("已有" + getString(R.string.text_fac_default) + getString(R.string.text_fac_tip2));
            show_Toast(getString(R.string.text_setFac_show1));//Existing manufacturer has been selected
            return false;
        }
        Defactory defactory = new Defactory();
        defactory.setDeName(name);
        defactory.setDeEntCode(code);
        defactory.setDeFeatureCode(te);
        defactory.setIsSelected(selected);
        getDaoSession().getDefactoryDao().insert(defactory);

        getLoaderManager().restartLoader(1, null, SetFactoryActivity.this);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;//这里的info.id对应的就是数据库中_id的值
        switch (item.getItemId()) {
            case 1:
                getDaoSession().getDefactoryDao().deleteByKey((long)id);
                getLoaderManager().restartLoader(1, null, SetFactoryActivity.this);
                break;
            case 2:
                this.modifyFactoryInfo(id);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void modifyFactoryInfo(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetFactoryActivity.this);
        // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.text_fac_tip3));//"请修改厂家信息"
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SetFactoryActivity.this).inflate(R.layout.factorymodifydialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText factoryname = (EditText) view.findViewById(R.id.factoryname);
        final EditText factorycode = (EditText) view.findViewById(R.id.factorycode);
        final EditText factoryfea = (EditText) view.findViewById(R.id.factoryfea);
        final CheckBox factoryselected = (CheckBox) view.findViewById(R.id.factoryselected);

        Defactory defactory = getDaoSession().getDefactoryDao().load((long) id);

        factoryname.setText(defactory.getDeName());
        factorycode.setText(defactory.getDeEntCode());
        factoryfea.setText(defactory.getDeFeatureCode());
        if (getString(R.string.text_setFac_yes).equals(defactory.getIsSelected())) {
            factoryselected.setChecked(true);
        }
        builder.setPositiveButton(getString(R.string.text_alert_sure), (dialog, which) -> {
            String a = factoryname.getText().toString().trim();
            String b = factorycode.getText().toString().trim();
            String c = factoryfea.getText().toString().trim();
            String d = getString(R.string.text_setFac_no);
            if (factoryselected.isChecked()) {
                d = getString(R.string.text_setFac_yes);
            }

            updateFactory(a, b, c, d, id);
            getLoaderManager().restartLoader(1, null, SetFactoryActivity.this);
            //    将输入的用户名和密码打印出来
            show_Toast(getString(R.string.text_error_tip38));
            hideInputKeyboard();
        });
        builder.setNegativeButton(getString(R.string.text_alert_cancel), (dialog, which) -> {

        });
        builder.show();
    }

    /**
     * 检查重复的数据
     */
    public boolean checkRepeatCode(String deEntCode) {
        GreenDaoMaster master = new GreenDaoMaster();
        if (master.queryDefactoryToDeEntCode(deEntCode).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkRepeatSelected(String selected) {
        GreenDaoMaster master = new GreenDaoMaster();
        if (master.queryDefactoryToIsSelected(selected).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void updateFactory(String name, String code, String fea, String selected, int id) {
        Defactory defactory = new Defactory();
        defactory.setId((long) id);
        defactory.setDeName(name);
        defactory.setDeEntCode(code);
        defactory.setDeFeatureCode(fea);
        defactory.setIsSelected(selected);
        getDaoSession().update(defactory);
        Utils.saveFile();//把软存中的数据存入磁盘中
    }

    private String checkData() {
        String tipStr = "";
        String st2Bit = et_factory_name.getText().toString();
        String stproDt = et_factory_code.getText().toString();
        if (!StringUtils.isNotBlank(st2Bit)) {
            return getString(R.string.text_error_tip58);//"厂家名不能为空";
        }
        if (!StringUtils.isNotBlank(stproDt)) {
            return getString(R.string.text_error_tip59);//"代码不能为空";
        }
        return tipStr;
    }

    @OnClick({R.id.btn_factory_return, R.id.btn_factory_inputOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_factory_return:
                Intent intentTemp = new Intent();
                intentTemp.putExtra("backString", "");
                setResult(1, intentTemp);
                finish();
                break;
            case R.id.btn_factory_inputOk:
                hideInputKeyboard();
                String checstr = checkData();
                if (checstr == null || checstr.trim().length() < 1) {
                    String facname = et_factory_name.getText().toString();
                    String faccode = et_factory_code.getText().toString();
                    String facTe = et_factory_feature.getText().toString().toUpperCase();//转成大写
                    String facselected = getString(R.string.text_setFac_no);
                    if (et_factory_selected.isChecked()) {
                        facselected = getString(R.string.text_setFac_yes);
                    }
                    if (insertFactory(facname, faccode, facTe, facselected)) {
                        show_Toast(getString(R.string.text_fac_tip1));
                    }
                } else {
                    show_Toast(checstr);
                }
                getLoaderManager().restartLoader(1, null, SetFactoryActivity.this);
                break;
        }
    }
}
