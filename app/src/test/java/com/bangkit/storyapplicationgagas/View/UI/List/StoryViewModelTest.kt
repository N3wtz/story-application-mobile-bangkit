package com.bangkit.storyapplicationgagas.View.UI.List

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.DataDummy
import com.bangkit.storyapplicationgagas.MainDispatcherRule
import com.bangkit.storyapplicationgagas.View.UI.MainViewModel
import com.bangkit.storyapplicationgagas.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        // Prepare the dummy data
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = PagingData.from(dummyStory)

        // Mock the repository method to return a Flow of PagingData
        val expectedFlow = flowOf(data)
        Mockito.`when`(storyRepository.getStoriesPaging()).thenReturn(expectedFlow)

        // Initialize the ViewModel
        val mainViewModel = StoryViewModel(storyRepository)

        // Collect the data from Flow
        val actualStory = mainViewModel.getStories().getOrAwaitValue()

        // Use AsyncPagingDataDiffer to compare the PagingData
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        // Assert the values
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        // Prepare empty data
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())

        // Mock the repository method to return a Flow of PagingData
        val expectedFlow = flowOf(data)
        Mockito.`when`(storyRepository.getStoriesPaging()).thenReturn(expectedFlow)

        // Initialize the ViewModel
        val mainViewModel = StoryViewModel(storyRepository)

        // Collect the data from Flow
        val actualStory = mainViewModel.getStories().getOrAwaitValue()

        // Use AsyncPagingDataDiffer to compare the PagingData
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        // Assert that no data is returned
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
