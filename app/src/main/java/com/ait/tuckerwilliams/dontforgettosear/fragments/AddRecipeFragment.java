package com.ait.tuckerwilliams.dontforgettosear.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ait.tuckerwilliams.dontforgettosear.MainActivity;
import com.ait.tuckerwilliams.dontforgettosear.R;
import com.ait.tuckerwilliams.dontforgettosear.adapter.AddRecipeAdaptor;
import com.ait.tuckerwilliams.dontforgettosear.data.Recipe;
import com.ait.tuckerwilliams.dontforgettosear.touch.ItemTouchHelperCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.realm.Realm;

//TODO: Provide auto-complete suggestions for ingredients.
//TODO: Consider doing FloatingActionButton for adding? Is that more Material?

public class AddRecipeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private static final String ARG_RECIPENAME = "recipeName";
    private static final String ARG_RECIPESERVINGS = "recipeServings";
    private static final String ARG_RECIPETYPE = "recipeType";
    private static final String ARG_RECIPE_ID = "recipeID";
    private String selectedImagePath;
    private String mRecipeName;
    private String mRecipeServings;
    private String mRecipeType;
    private String mRecipeID;

    private Activity activity;
    private Context context;
    private Realm realm;

    private AddRecipeAdaptor mAdapterIngredients;
    private AddRecipeAdaptor mAdapterDirections;

    private OnSingleRecipeFragmentInteractionListener mListener;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    public static AddRecipeFragment newInstance(String mRecipeName, String mRecipeServings, String mRecipeType) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPENAME, mRecipeName);
        args.putString(ARG_RECIPESERVINGS, mRecipeServings);
        args.putString(ARG_RECIPETYPE, mRecipeType);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddRecipeFragment newInstance(Recipe recipe) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPENAME, recipe.getRecipeName());
        args.putString(ARG_RECIPESERVINGS, recipe.getRecipeServings());
        args.putString(ARG_RECIPETYPE, recipe.getRecipeType());
        args.putString(ARG_RECIPE_ID, recipe.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipeName = getArguments().getString(ARG_RECIPENAME);
            mRecipeServings = getArguments().getString(ARG_RECIPESERVINGS);
            mRecipeType = getArguments().getString(ARG_RECIPETYPE);
            mRecipeID = getArguments().getString(ARG_RECIPE_ID);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        RecyclerView rvIng = (RecyclerView) view.findViewById(R.id.addedIngredientsRecyclerView);
        RecyclerView rvDirections = (RecyclerView) view.findViewById(R.id.addedDirectionsRecyclerView);

        ImageView imgPlusIngredient = (ImageView) view.findViewById(R.id.imgPlusIngredient);
        ImageView imgPlusDirection = (ImageView) view.findViewById(R.id.imgPlusDirection);

        setRecipeImage(view);

        setupImagePlusListener(inflater, imgPlusIngredient);
        setupImagePlusDirectionListener(inflater, imgPlusDirection);

        setupToolbar();
        setupRecyclerView(view, rvIng, rvDirections);

        return view;
    }

    private void setupImagePlusDirectionListener(final LayoutInflater inflater,
                                                 ImageView imgPlusDirection) {
        imgPlusDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View popup = inflater.inflate(R.layout.alertdialog_add_direction, null);
                final MaterialDialog.Builder mdb1 = new MaterialDialog.Builder(context)
                        .customView(popup, true);
                mdb1.autoDismiss(true);

                mdb1.positiveText("Save");
                mdb1.negativeText("Cancel");

                mdb1.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        EditText etDirection = (EditText) popup.findViewById(R.id.etAddDirection);

                        if (!etDirection.getText().toString().equals("")) {
                            mAdapterDirections.addStep(etDirection.getText().toString());
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                });

                mdb1.show();
            }
        });
    }

    private void setupImagePlusListener(final LayoutInflater inflater, ImageView imgPlusIngredient) {
        imgPlusIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View popup = inflater.inflate(R.layout.alertdialog_add_ingredient, null);
                final MaterialDialog.Builder mdb1 = new MaterialDialog.Builder(context)
                        .customView(popup, true);
                mdb1.autoDismiss(true);

                mdb1.positiveText("Save");
                mdb1.negativeText("Cancel");

                mdb1.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        EditText etIngredient = (EditText) popup.findViewById(R.id.etAddIngredient);
                        EditText etQuantity = (EditText) popup.findViewById(R.id.etAddQuantity);

                        if (!etIngredient.getText().toString().equals("") &&
                                !etQuantity.getText().toString().equals("")) {
                            mAdapterIngredients.addIngredient(etIngredient.getText().toString(),
                                    etQuantity.getText().toString());
                        }
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                });

                mdb1.show();
            }
        });
    }


    private void setRecipeImage(View view) {
        ImageView imgRecipe = (ImageView) view.findViewById(R.id.imgRecipePicture);
        imgRecipe.setImageResource(setRecipeImage(mRecipeType));
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbarTop);
        toolbar.setTitle("");
        //toolbar.setDisplayShowTitleEnabled(false); //TODO-// FIXME: 5/10/17
        TextView tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        if (mRecipeID == null)
            tbTitle.setText(R.string.tb_adding_recipe);
        else
            tbTitle.setText(mRecipeName);

        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView(View view, RecyclerView rvIng, RecyclerView rvDir) {

        if (mRecipeID != null) {
            //then we are calling AddRecipeFragment from DisplayRecipeFragment
            realm.beginTransaction();
            Recipe recipe = realm.where(Recipe.class).equalTo("id", mRecipeID).findFirst();
            mAdapterIngredients = new AddRecipeAdaptor(context, "INGREDIENT", recipe.getmIngredientList(), null);
            mAdapterDirections = new AddRecipeAdaptor(context, "DIRECTION", null, recipe.getmDirectionList());
        } else {
            mAdapterIngredients = new AddRecipeAdaptor(context, "INGREDIENT", null, null);
            mAdapterDirections = new AddRecipeAdaptor(context, "DIRECTION", null, null);
            //mAdapter = new AddRecipeAdaptor(context);
        }

        LinearLayoutManager llm1 = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);
        LinearLayoutManager llm2 = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);

        rvIng.setLayoutManager(llm1);
        rvIng.setItemAnimator(new DefaultItemAnimator());

        rvDir.setLayoutManager(llm2);
        rvDir.setItemAnimator(new DefaultItemAnimator());

        rvIng.setAdapter(mAdapterIngredients);
        rvDir.setAdapter(mAdapterDirections);

        setupTouchSupport(rvIng, mAdapterIngredients);
        setupTouchSupport(rvDir, mAdapterDirections);
    }

    private void setupTouchSupport(RecyclerView rv, AddRecipeAdaptor adapter) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_add_recipe, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_opensettings:
                ((MainActivity) activity).openSettingsFragment();
                return true;
            case R.id.action_change_recipe_picture:
                changeRecipePicture();
                return true;
        }

        return false;
    }

    private void changeRecipePicture() {

        MaterialDialog.Builder mdb = new MaterialDialog.Builder(context)
                .title("Upload a new picture")
                .positiveText("Camera")
                .negativeText("Galery")
                .neutralText("Cancel");

        mdb.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

//                Intent intent = new Intent(
//                        MediaStore.ACTION_IMAGE_CAPTURE);
//
//                startActivityForResult(intent,
//                        CAMERA_REQUEST);

                Toast.makeText(context, "To be implemented...", Toast.LENGTH_SHORT).show();

            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

//                Intent pictureActionIntent;
//
//                pictureActionIntent = new Intent(
//                        Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(
//                        pictureActionIntent,
//                        GALLERY_REQUEST);

                Toast.makeText(context, "To be implemented...", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialDialog dialog = mdb.build();
        dialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSingleRecipeFragmentInteraction();
        }
    }

    private int setRecipeImage(String type) {
        switch (type) {
            case "Burgers":
                return (R.drawable.burgers_thumbnail);
            case "Beans, Grains, Legume":
                return (R.drawable.bean_thumbnail);
            case "Bread, Rolls, Muffins":
                return (R.drawable.bread_roll_thumbnail);
            case "Cakes, Cupcakes":
                return (R.drawable.cakes_thumbnail);
            case "Cocktail":
                return (R.drawable.cocktails_thumbnail);
            case "Desserts":
                return (R.drawable.desserts_thumbail);
            case "Non-alcoholic drinks":
                return (R.drawable.nonalcoholicdrink_thumbnail);
            case "Pasta, Noodles":
                return (R.drawable.pasta_thumbnail);
            case "Pies":
                return (R.drawable.pies_thumbnail);
            case "Pizza":
                return (R.drawable.pizzas_thumbnail);
            case "Salad":
                return (R.drawable.salad_thumbnail);
            case "Sandwiches":
                return (R.drawable.sandwiches_thumbnail);
            case "Soups, Stews":
                return (R.drawable.soups_thumnail);
            default:
                return (R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = getContext();
        realm = Realm.getDefaultInstance();

        if (context instanceof OnSingleRecipeFragmentInteractionListener) {
            mListener = (OnSingleRecipeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSingleRecipeFragmentInteractionListener");
        }
    }

    //Todo: Is this the best way to handle recipe saving? On detach? What about super.onBackPressed()?
    @Override
    public void onDetach() {

        mListener = null;
        saveRecipeOnExit();
        super.onDetach();
    }

    @Override
    public void onPause() {
        saveRecipeOnExit();
        super.onPause();
    }

    public void saveRecipeOnExit() {
        if (mRecipeID == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //Todo: Make sure UUID is same when in fragment window. E.g., reset upon exit.
                    //Todo: Only create a user if we don't have one.
                    Recipe recipe = realm.createObject(Recipe.class, UUID.randomUUID().toString());
                    recipe.setRecipeName(mRecipeName);
                    recipe.getmIngredientList().addAll(mAdapterIngredients.getmIngredientList());
                    recipe.getmDirectionList().addAll(mAdapterDirections.getmDirectionList());
                    recipe.setRecipeType(mRecipeType);
                    recipe.setRecipeServings(mRecipeServings);
                }
            });
        } else {
            if (!realm.isInTransaction())
                realm.beginTransaction();
            Recipe recipe = realm.where(Recipe.class).equalTo("id", mRecipeID).findFirst();
            recipe.setRecipeName(mRecipeName);

            //Inefficient. But what should I do instead? Add a "NEW" boolean field?

            recipe.getmIngredientList().clear();
            recipe.getmIngredientList().addAll(mAdapterIngredients.getmIngredientList());

            recipe.getmDirectionList().clear();
            recipe.getmDirectionList().addAll(mAdapterDirections.getmDirectionList());

            recipe.setRecipeType(mRecipeType);
            recipe.setRecipeServings(mRecipeServings);
            recipe.setRecipeImg(selectedImagePath);

            realm.commitTransaction();
        }
    }

    public interface OnSingleRecipeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSingleRecipeFragmentInteraction();

    }
}
