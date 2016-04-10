package com.lue.laoyoutiao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.lue.laoyoutiao.R;

/**
 * Created by Lue on 2016/4/10.
 */
public class LoadingDialog extends Dialog
{
    private TextView textview_loading;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        View view = inflater.inflate(R.layout.dialog_loading, container);
//        return view;
//    }

    public LoadingDialog(Context context)
    {
        super(context, R.style.loadingDialogStyle);
    }

    public LoadingDialog(Context context, int theme)
    {
        super(context, theme);
    }

    protected LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        textview_loading = (TextView)this.findViewById(R.id.loading_textview);
        textview_loading.setText("正在获取内容，请稍侯...");
//        LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.loading_dialog_linearlayout);
//        linearLayout.getBackground().setAlpha(100);
    }
}
