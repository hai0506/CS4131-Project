package com.example.grades.Misc;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.grades.Models.Station;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    MutableLiveData<String> temperature = new MutableLiveData<>();
    MutableLiveData<String> wind = new MutableLiveData<>();
    MutableLiveData<String> humidity = new MutableLiveData<>();
    MutableLiveData<String> weather = new MutableLiveData<>();
    MutableLiveData<String> psi = new MutableLiveData<>();
    MutableLiveData<String> uv = new MutableLiveData<>();
    MutableLiveData<String> forecast = new MutableLiveData<>();
    MutableLiveData<ArrayList<Station>> stations = new MutableLiveData<>();
    MutableLiveData<String> windUnit = new MutableLiveData<>();
    MutableLiveData<String> tempUnit = new MutableLiveData<>();
    MutableLiveData<Boolean> internet = new MutableLiveData<>();
    MutableLiveData<Long> time = new MutableLiveData<>();



    public void setTemperature(String t) {
        temperature.postValue(t);
    }
    public void setWind(String t) {
        wind.postValue(t);
    }
    public void setHumidity(String t) {
        humidity.postValue(t);
    }
    public void setWeather(String t) {
        weather.postValue(t);
    }
    public void setPsi(String t) {
        psi.postValue(t);
    }
    public void setUv(String t) {
        uv.postValue(t);
    }
    public void setForecast(String t) {
        forecast.postValue(t);
    }
    public void setStations(ArrayList<Station> t) {
        stations.postValue(t);
    }
    public void setWindUnit(String t) {
        windUnit.postValue(t);
    }
    public void setTempUnit(String t) {
        tempUnit.postValue(t);
    }
    public void setInternet(boolean t) {
        internet.postValue(t);
    }
    public void setTime(long t) {
        time.postValue(t);
    }

    public MutableLiveData<String> getTemperature() { return temperature; }
    public MutableLiveData<String> getWind() {
        return wind;
    }
    public MutableLiveData<String> getHumidity() { return humidity; }
    public MutableLiveData<String> getWeather() { return weather; }
    public MutableLiveData<String> getPsi() { return psi; }
    public MutableLiveData<String> getUv() { return uv; }
    public MutableLiveData<String> getForecast() { return forecast; }
    public MutableLiveData<ArrayList<Station>> getStations() { return stations; }
    public MutableLiveData<String> getWindUnit() { return windUnit; }
    public MutableLiveData<String> getTempUnit() { return tempUnit; }
    public MutableLiveData<Boolean> getInternet() { return internet; }
    public MutableLiveData<Long> getTime() { return time; }
}
