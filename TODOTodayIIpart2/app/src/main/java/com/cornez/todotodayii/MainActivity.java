package com.cornez.todotodayii;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    protected DBHelper mDBHelper;
    private List<ToDo_Item> list;
    private MyAdapter adapt;
    private EditText myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TASK 1: LAUNCH THE LAYOUT REPRESENTING THE MAIN ACTIVITY
        setContentView(R.layout.activity_main);

        // TASK 2: ESTABLISH REFERENCES TO THE UI
        //      ELEMENTS LOCATED ON THE LAYOUT
        myTask = (EditText) findViewById(R.id.editText1);

        // TASK 3: SET UP THE DATABASE
        mDBHelper = new DBHelper(this);
        /*
        list = mDBHelper.getAllTasks();
        adapt = new MyAdapter(this, R.layout.todo_item, list);
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);
        */
    }

    @Override
    protected void onResume(){
        super.onResume();
        list = mDBHelper.getAllTasks();
        adapt = new MyAdapter(this, R.layout.todo_item, list);
        ListView listTask = (ListView) findViewById(R.id.listView1);
        listTask.setAdapter(adapt);
    }

    //BUTTON CLICK EVENT FOR ADDING A TODO TASK
    public void addTaskNow(View view) {
        String s = myTask.getText().toString();
        if (s.isEmpty()) {
            Toast.makeText(getApplicationContext(), "A TODO task must be entered.", Toast.LENGTH_SHORT).show();
        } else {

            //BUILD A NEW TASK ITEM AND ADD IT TO THE DATABASE
            ToDo_Item task = new ToDo_Item(s, 0);
            mDBHelper.addToDoItem(task);

            // CLEAR OUT THE TASK EDITVIEW
            myTask.setText("");

            // ADD THE TASK AND SET A NOTIFICATION OF CHANGES
            adapt.add(task);
            adapt.notifyDataSetChanged();
        }
    }

    //BUTTON CLICK EVENT FOR DELETING ALL TODO TASKS
    public void clearTasks(View view) {
        mDBHelper.clearAll(list);
        adapt.notifyDataSetChanged();
    }
//    public void deleteTask(View view) {
//        ToDo_Item item = new ToDo_Item();
//        mDBHelper.getAllTasks();
//        item = myTask;
//        //item.getId() = view.getId();
//        mDBHelper.deleteItem(item);
//        adapt.notifyDataSetChanged();
//    }

    //******************* ADAPTER ******************************
    private class MyAdapter extends ArrayAdapter<ToDo_Item> {
        Context context;
        List<ToDo_Item> taskList = new ArrayList<ToDo_Item>();

        public MyAdapter(Context c, int rId, List<ToDo_Item> objects) {
            super(c, rId, objects);
            taskList = objects;
            context = c;
        }

    //******************* TODO TASK ITEM VIEW ******************************
        /**
         * THIS METHOD DEFINES THE TODO ITEM THAT WILL BE PLACED
         * INSIDE THE LIST VIEW.
         *
         * THE CHECKBOX STATE IS THE IS_DONE STATUS OF THE TODO TASK
         * AND THE CHECKBOX TEXT IS THE TODO_ITEM TASK DESCRIPTION.
         */





        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            CheckBox isDoneChBx = null;
//            Button dltButton = null;
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.todo_item, parent, false);
                holder = new ViewHolder();
                holder.isDone = (CheckBox) convertView.findViewById(R.id.chkStatus);
                holder.toDelete = (Button) convertView.findViewById(R.id.buttonDelete);
//                isDoneChBx = (CheckBox) convertView.findViewById(R.id.chkStatus);
//                dltButton = (Button) convertView.findViewById(R.id.buttonDelete);

                //convertView.setTag(holder);

//                convertView.setTag(dltButton);
//                convertView.setTag(isDoneChBx);

                holder.toDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Button bb = (Button) view;
                        ToDo_Item deleteTask = (ToDo_Item) bb.getTag();

                        deleteTask.setIs_done(bb.isPressed() == true ? 1 : 0);
                        mDBHelper.deleteItem(deleteTask);

                        adapt.notifyDataSetChanged();
                        list.remove(deleteTask);
                    }

                });

                holder.isDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        ToDo_Item changeTask = (ToDo_Item) cb.getTag();
                        changeTask.setIs_done(cb.isChecked() == true ? 1 : 0);
                        mDBHelper.updateTask(changeTask);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            ToDo_Item current = taskList.get(position);
            holder.isDone.setText(current.getDescription());
            holder.isDone.setChecked(current.getIs_done() == 1 ? true : false);
            holder.isDone.setTag(current);
            holder.toDelete.setPressed(current.getIs_done() == 1 ? true : false);
            holder.toDelete.setTag(current);

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    static class ViewHolder {
        private CheckBox isDone;
        private Button toDelete;
    }
}
