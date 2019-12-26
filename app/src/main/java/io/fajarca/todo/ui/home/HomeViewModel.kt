package io.fajarca.todo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.fajarca.todo.domain.model.local.Character
import io.fajarca.todo.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _characters = MutableLiveData<Result<List<Character>>>()
    val characters: LiveData<Result<List<Character>>>
        get() = _characters

    sealed class Result<out T> {
        object Loading : Result<Nothing>()
        data class Success<out T>(val data : T): Result<T>()
        data class Error(val throwable: Throwable) : Result<Nothing>()
    }

    fun getAllCharacters() {
        _characters.postValue(Result.Loading)
        viewModelScope.launch {
            getCharactersUseCase.execute()
                .collect {
                    _characters.postValue(Result.Success(it))
                }
        }
    }
}