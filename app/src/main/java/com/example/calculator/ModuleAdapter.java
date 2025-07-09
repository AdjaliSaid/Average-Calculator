package com.example.calculator;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {
    private List<Module> moduleList;
    ModuleAdapter(List<Module> moduleList) {
        this.moduleList=moduleList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_module,parent,false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.moduleName.setText(moduleList.get(position).getName());
        holder.moduleCoef.setText(moduleList.get(position).getCoef()+"");
        holder.moduleCred.setText(moduleList.get(position).getCred()+"");
        holder.tp.setHintTextColor(Color.RED);
        holder.td.setHintTextColor(Color.RED);
        holder.exam.setHintTextColor(Color.RED);

        holder.tp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                float text =Float.parseFloat(holder.tp.getText().toString());
                if(text<=20 && text>=0) {
                    moduleList.get(position).setTp(text);
                    if(!isTextViewEmpty(holder.exam) && !isTextViewEmpty(holder.td) && !isTextViewEmpty(holder.tp)) {
                        float a=(moduleList.get(position).getTd()+moduleList.get(position).getTp()/2) + moduleList.get(position).getExam() / 2;
                        moduleList.get(position).setAverage(a);
                        holder.moduleMoy.setText(a+"");
                    }
                }else {
                    holder.tp.setText("");
                }
            }
        });
        holder.td.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                float text =Float.parseFloat(holder.td.getText().toString());
                if(text<=20 && text>=0) {
                    moduleList.get(position).setTd(text);
                    if(!isTextViewEmpty(holder.exam) && !isTextViewEmpty(holder.td) && !isTextViewEmpty(holder.tp)) {
                        float a=(moduleList.get(position).getTd()+moduleList.get(position).getTp()/2) + moduleList.get(position).getExam() / 2;
                        moduleList.get(position).setAverage(a);
                        holder.moduleMoy.setText(a+"");
                    }
                }else {
                    holder.td.setText("");
                }
            }
        });
        holder.exam.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                float text =Float.parseFloat(holder.exam.getText().toString());
                if(text<=20 && text>=0) {
                    moduleList.get(position).setExam(text);
                    if(!isTextViewEmpty(holder.exam) && !isTextViewEmpty(holder.td) && !isTextViewEmpty(holder.tp)) {
                        float a=(moduleList.get(position).getTd()+moduleList.get(position).getTp()/2) + moduleList.get(position).getExam() / 2;
                        moduleList.get(position).setAverage(a);
                        holder.moduleMoy.setText(a+"");
                    }
                }else {
                    holder.exam.setText("");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView moduleName,moduleCoef,moduleCred,moduleMoy;
        TextView tp,td,exam;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName=itemView.findViewById(R.id.moduleName);
            moduleCoef=itemView.findViewById(R.id.moduleCoef);
            moduleCred=itemView.findViewById(R.id.moduleCred);
            tp = itemView.findViewById(R.id.tpEditText);
            td = itemView.findViewById(R.id.tdEditText);
            exam = itemView.findViewById(R.id.examEditText);
            moduleMoy = itemView.findViewById(R.id.moduleMoy);
        }
    }
    // Method to check if TextView is empty
    private boolean isTextViewEmpty(TextView textView) {
        return textView.getText().toString().trim().isEmpty();
    }
}
