package com.chandan.apnaacoaching.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chandan.apnaacoaching.data.CommunityComment
import com.chandan.apnaacoaching.data.CommunityGroup
import com.chandan.apnaacoaching.data.CommunityReply
import com.chandan.apnaacoaching.data.CommunityThread
import com.chandan.apnaacoaching.remote.RetrofitClient.CommunityApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {

    // --- STATE MANAGEMENT ---

    // Holds the list of threads for the UI to observe
    private val _threads = MutableStateFlow<List<CommunityThread>>(emptyList())
    val threads: StateFlow<List<CommunityThread>> = _threads.asStateFlow()

    // Holds error messages (can be observed to show a Snack bar or Toast)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _currentThread = MutableStateFlow<CommunityThread?>(null)
    val currentThread: StateFlow<CommunityThread?> = _currentThread.asStateFlow()

    private val _replies = MutableStateFlow<List<CommunityReply>>(emptyList())
    val replies: StateFlow<List<CommunityReply>> = _replies.asStateFlow()

    private val _groups = MutableStateFlow<List<CommunityGroup>>(emptyList())
    val groups: StateFlow<List<CommunityGroup>> = _groups.asStateFlow()

    private val _currentReplyForComments = MutableStateFlow<CommunityReply?>(null)
    val currentReplyForComments: StateFlow<CommunityReply?> = _currentReplyForComments.asStateFlow()

    private val _comments = MutableStateFlow<List<CommunityComment>>(emptyList())
    val comments: StateFlow<List<CommunityComment>> = _comments.asStateFlow()

    // --- NEW PAGINATION STATE FOR THREADS ---
    private var currentPage = 1
    private var isLastPage = false

    // Tracks if we are loading the VERY FIRST page
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Tracks if we are loading SUBSEQUENT pages (so we don't show a full-screen spinner)
    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating.asStateFlow()
    // --- ACTIONS ---

    /**
     * Fetches all threads for a specific group from api_community.php
     */
    fun fetchThreads(groupId: String, isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            isLastPage = false
            _isLoading.value = true
        } else {
            // Prevent duplicate calls if we are already paginating or reached the end
            if (isLastPage || _isPaginating.value || _isLoading.value) return
            _isPaginating.value = true
        }

        viewModelScope.launch {
            try {
                val response = CommunityApi.getThreads(groupId, currentPage)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val newThreads = response.body()?.threads ?: emptyList()

                    if (isRefresh) {
                        _threads.value = newThreads
                    } else {
                        // Append new items to the bottom of the existing list
                        val currentList = _threads.value.toMutableList()
                        currentList.addAll(newThreads)
                        _threads.value = currentList
                    }

                    // Update pagination flags based on server response
                    isLastPage = !(response.body()?.has_more ?: false)
                    if (!isLastPage) currentPage++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                _isPaginating.value = false
            }
        }
    }
    /**
     * Posts a new question to the server and refreshes the list on success
     */
    fun createThread(
        groupId: String,
        userId: String,
        title: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = CommunityApi.createThread(groupId, userId, title, description)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchThreads(groupId, isRefresh = true) // Force refresh from page 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add this function to fetch replies
    fun fetchReplies(threadId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = CommunityApi.getReplies(threadId, userId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    _currentThread.value = response.body()?.thread
                    _replies.value = response.body()?.replies ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ADD THIS NEW FUNCTION
    fun toggleLike(replyId: String, userId: String) {
        // 1. Optimistic Update: Change the UI instantly
        val currentList = _replies.value.toMutableList()
        val index = currentList.indexOfFirst { it.reply_id == replyId }

        if (index != -1) {
            val reply = currentList[index]
            val wasLiked = reply.is_liked_by_me

            // Create a modified copy of the reply
            val updatedReply = reply.copy(
                is_liked_by_me = !wasLiked,
                like_count = if (wasLiked) reply.like_count - 1 else reply.like_count + 1
            )

            currentList[index] = updatedReply
            _replies.value = currentList // Triggers UI recomposition instantly

            // 2. Network Call: Tell the server in the background
            viewModelScope.launch {
                try {
                    val response = CommunityApi.toggleLike(replyId, userId)
                    if (response.isSuccessful && response.body()?.status == "success") {
                        // Sync exact count from server just in case
                        val exactCount = response.body()?.like_count ?: updatedReply.like_count
                        currentList[index] = updatedReply.copy(like_count = exactCount)
                        _replies.value = ArrayList(currentList)
                    } else {
                        // Revert if server failed
                        currentList[index] = reply
                        _replies.value = ArrayList(currentList)
                    }
                } catch (e: Exception) {
                    // Revert if network failed
                    currentList[index] = reply
                    _replies.value = ArrayList(currentList)
                }
            }
        }
    }

    // Add this function to post a reply
    fun postReply(threadId: String, userId: String, replyText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = CommunityApi.createReply(userId = userId, reply = replyText, threadId = threadId)
                if (response.isSuccessful) {
                    fetchReplies(threadId,userId) // Refresh the list instantly
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = CommunityApi.getGroups()
                if (response.isSuccessful && response.body()?.status == "success") {
                    _groups.value = response.body()?.groups ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchComments(replyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = CommunityApi.getComments(replyId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    _currentReplyForComments.value = response.body()?.reply
                    _comments.value = response.body()?.comments ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun postComment(userId: String, replyId: String, threadId: String, commentText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = CommunityApi.createComment(userId, replyId, threadId, commentText)
                if (response.isSuccessful) {
                    fetchComments(replyId) // Refresh list instantly
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}