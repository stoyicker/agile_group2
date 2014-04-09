package org.arnolds.agileappproject.agileappmodule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.arnolds.agileappproject.agileappmodule.git.GitHubBroker;
import org.arnolds.agileappproject.agileappmodule.git.GitHubBrokerListener;
import org.arnolds.agileappproject.agileappmodule.git.IGitHubBrokerListener;
import org.kohsuke.github.GHRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The MIT License (MIT)
 * <p/>
 * <p/>
 * <p/>
 * Copyright (c) 2014 agile_arnolds
 * <p/>
 * <p/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * <p/>
 * of this software and associated documentation files (the "Software"), to deal
 * <p/>
 * in the Software without restriction, including without limitation the rights
 * <p/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * <p/>
 * copies of the Software, and to permit persons to whom the Software is
 * <p/>
 * furnished to do so, subject to the following conditions:
 * <p/>
 * <p/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * <p/>
 * all copies or substantial portions of the Software.
 * <p/>
 * <p/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * <p/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * <p/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * <p/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * <p/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * <p/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * <p/>
 * THE SOFTWARE.
 */
public class SelectRepositoryFragment extends FragmentActivity {

    private RepoSpinAdapter repoAdapter;
    Spinner repoSpinner;


    private final IGitHubBrokerListener repoListener = new RepoListener();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(name, context, attrs);
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_repo_list);

        repoSpinner = (Spinner) findViewById(R.id.repo_spinner);
        repoAdapter = new RepoSpinAdapter();

        repoSpinner.setAdapter(repoAdapter);
        Log.d("debug", "hago algo aqui");

        repoSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "selected repo " + position);
                // SelectRepo(position);
                /*


                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("debug", "no selected repo ");
            }
        });

        try {
            GitHubBroker.getInstance().addSubscriber(repoListener);
        } catch (GitHubBroker.NullArgumentException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        } catch (GitHubBroker.ListenerAlreadyRegisteredException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            GitHubBroker.getInstance().getAllRepos();
        } catch (GitHubBroker.AlreadyNotConnectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    private void onSelectRepo(int position) {
        GHRepository repoToSelect = repoAdapter.getItem(position);
        try {
            GitHubBroker.getInstance().selectRepo(repoToSelect);
        } catch (GitHubBroker.AlreadyNotConnectedException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        } catch (GitHubBroker.NullArgumentException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
    }

    private void onReposRetrieved(Collection<GHRepository> repos) {

        repoAdapter.getRepoCollection().addAll(repos);
        for (GHRepository r : repos) {
            Log.d("debug", "repo" + r.getName());
        }


    }


    private final class RepoSpinAdapter extends BaseAdapter {
        private final List<GHRepository> repoCollection = new LinkedList<GHRepository>();

        public List<GHRepository> getRepoCollection() {
            return repoCollection;
        }

        @Override
        public GHRepository getItem(int position) {
            return repoCollection.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return repoCollection.size();
        }

       /* @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }
        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }
        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.fragment_repo_list, parent,
                    false);
           // TextView main_text = (TextView) mySpinner
                    .findViewById(R.id.name_repo);

            //main_text.setText(spispinnerValues[position]);

            GHRepository repository = getItem(position);
            TextView subSpinner = (TextView) mySpinner
                    .findViewById(R.id.repo_spinner);

            subSpinner.setText(repository.getName());



            return repoSpinner;
        }
*/

/*
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.fragment_item_repo_list, null);
                viewHolder = new ViewHolder();
                TextView main_text = (TextView) repoSpinner
                        .findViewById(R.id.name_repo);
                viewHolder.setNameView(main_text);


                 viewHolder.setNameView((TextView) convertView.findViewById(R.id.name_repo));

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHRepository repository = getItem(position);
            viewHolder.getNameView().setText(repository.getName());

            return convertView;
        }

        /*
               @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                   ViewHolder viewHolder;
                   if (convertView == null) {
                       convertView = ((LayoutInflater) getApplicationContext()
                               .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                               .inflate(R.layout.fragment_item_repo_list, null);
                       viewHolder = new ViewHolder();
                       TextView main_text = (TextView) repoSpinner
                               .findViewById(R.id.name_repo);
                       viewHolder.setNameView(main_text);


                        viewHolder.setNameView((TextView) convertView.findViewById(R.id.name_repo));

                       convertView.setTag(viewHolder);
                   } else {
                       viewHolder = (ViewHolder) convertView.getTag();
                   }
                   GHRepository repository = getItem(position);
                   viewHolder.getNameView().setText(repository.getName());

                   return convertView;

               }
        */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /*LayoutInflater inflater = getLayoutInflater();
            View viewSpinner = inflater.inflate(R.layout.fragment_repo_list, parent,
                    false);

            Log.d("debug", "i get into getView " + viewSpinner.toString());
            TextView text = (TextView) viewSpinner.findViewById(R.id.name_repo);
            //                     .findViewById(R.id.repo_spinner);
            Log.d("debug", "i get into getView " + text);
            text.setText(R.);
            return viewSpinner;*/
            //return getCustomView(position, convertView, parent);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.fragment_item_repo_list, null);
                viewHolder = new ViewHolder();
                viewHolder.setNameView((TextView) convertView.findViewById(R.id.name_repo));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHRepository repository = getItem(position);

            viewHolder.getNameView().setText(repository.getName());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
           /* LayoutInflater inflater = getLayoutInflater();
            View viewSpinner = inflater.inflate(R.layout.fragment_item_repo_list, parent,
                    false);
            TextView main_text = (TextView) viewSpinner
                    .findViewById(R.id.name_repo);
            main_text.setText(repoAdapter.getItem(position).getName());
            Log.d("debug", "i create drop down item " + repoAdapter.getItem(position).getName() + "in position"
                    + repoAdapter.getItemId(position));
            return viewSpinner;*/
            // return getCustomView(position, convertView, parent);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.fragment_item_repo_list, null);
                viewHolder = new ViewHolder();
                viewHolder.setNameView((TextView) convertView.findViewById(R.id.name_repo));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GHRepository repository = getItem(position);
            viewHolder.getNameView().setText(repository.getName());

            return convertView;
        }

      /*  public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View viewSpinner = inflater.inflate(R.layout.fragment_item_repo_list, parent,
                    false);
            TextView main_text = (TextView) viewSpinner
                    .findViewById(R.id.name_repo);
            main_text.setText(repoAdapter.getItem(position).getName());
            Log.d("debug", "i show item " + repoAdapter.getItem(position).getName() + "in position"
                    + repoAdapter.getItemId(position));
            posRepoChosen = (int) repoAdapter.getItemId(position);
            Log.d("debug","pos repo chosen "+posRepoChosen);
            return viewSpinner;


        }*/

        private final class ViewHolder {
            private TextView nameRepo;

            public TextView getNameView() {
                return nameRepo;
            }

            public void setNameView(TextView nameView) {
                this.nameRepo = nameView;
            }
        }
    }

    private final class RepoListener extends GitHubBrokerListener {
        @Override
        public void onRepoSelected(boolean result) {
            if (result) {
                Log.d("debug", "repo chosen");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "repo selected", Toast.LENGTH_LONG);
                    }
                });
            } else {

            }
        }

        @Override
        public void onAllReposRetrieved(boolean success, Collection<GHRepository> repos) {
            if (success) {
                onReposRetrieved(repos);
            } else {
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      Toast.makeText(getApplicationContext(), R.string.error_get_repos, Toast.LENGTH_LONG);
                                  }
                              }
                );

            }

        }
    }
}
