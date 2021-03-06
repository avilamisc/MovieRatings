package com.fenchtose.movieratings.features.search_page

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fenchtose.movieratings.MovieRatingsApplication
import com.fenchtose.movieratings.R
import com.fenchtose.movieratings.base.BaseFragment
import com.fenchtose.movieratings.base.RouterPath
import com.fenchtose.movieratings.model.Movie
import com.fenchtose.movieratings.model.api.provider.RetrofitMovieProvider
import com.fenchtose.movieratings.model.image.GlideLoader
import com.fenchtose.movieratings.model.image.PicassoLoader
import com.fenchtose.movieratings.util.Constants
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SearchPageFragment : BaseFragment(), SearchPage {

    private var progressbar: ProgressBar? = null
    private var attributeView: TextView? = null
    private var searchView: EditText? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: SearchPageAdapter? = null

    private var presenter: SearchPresenter? = null

    private var watcher: TextWatcher? = null
    private var querySubject: PublishSubject<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.search_page_layout, container, false)

        val gson = GsonBuilder().setDateFormat("dd MM yyyy").create()

        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.OMDB_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val dao = MovieRatingsApplication.getDatabase().movieDao()
        presenter = SearchPresenter(RetrofitMovieProvider(retrofit, dao))

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressbar = view.findViewById(R.id.progressbar) as ProgressBar
        attributeView = view.findViewById(R.id.api_attr) as TextView
        recyclerView = view.findViewById(R.id.recyclerview) as RecyclerView
        searchView = view.findViewById(R.id.search_view) as EditText

        val adapter = SearchPageAdapter(context, GlideLoader(Glide.with(this)))
        adapter.setHasStableIds(true)
        recyclerView?.adapter = adapter
//        recyclerView?.layoutManager = GridLayoutManager(context, 2)
        recyclerView?.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        recyclerView?.visibility = View.GONE
        this.adapter = adapter

        presenter?.attachView(this)

        watcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable) {
                querySubject?.onNext(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        }

        val subject: PublishSubject<String> = PublishSubject.create()
        val d = subject.debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (it.isEmpty()) {
                        clearData()
                        showLoading(false)
                    }
                }.filter { it.length > 4 }
                .subscribeBy(
                        onNext = {
                            presenter?.onSearchRequested(it)
                        }
                )

        subscribe(d)
        this.querySubject = subject
        searchView?.addTextChangedListener(watcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.detachView(this)
        watcher?.let {
            searchView?.removeTextChangedListener(watcher)
        }
        querySubject?.onComplete()
    }

    override fun canGoBack(): Boolean {
        return true
    }

    override fun getScreenTitle(): Int {
        return R.string.search_page_title
    }

    override fun showLoading(status: Boolean) {
        if (status) {
            progressbar?.visibility = View.VISIBLE
            attributeView?.visibility = View.GONE
            recyclerView?.visibility = View.GONE
        } else {
            attributeView?.visibility = View.VISIBLE
            progressbar?.visibility = View.GONE
        }
    }

    override fun setData(movies: ArrayList<Movie>) {
        showLoading(false)
        recyclerView?.visibility = View.VISIBLE
        adapter?.setData(movies)
        adapter?.notifyDataSetChanged()
    }

    override fun clearData() {
        adapter?.clearData()
        recyclerView?.visibility = View.GONE
    }

    class SearchPath : RouterPath<SearchPageFragment>() {
        override fun createFragmentInstance(): SearchPageFragment {
            return SearchPageFragment()
        }
    }
}