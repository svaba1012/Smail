package com.example.mailclient;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class DatePickController implements Initializable {
    public Slider timeSlider;

    public Integer hour;
    public Integer minutes;
    public Slider hourSlider;
    public Slider minuteSlider;
    public TextField timeText;
    public Button btnArrange;
    public Label lblArangeError;
    public DatePicker datePicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        hourSlider.setMin(0);
        hourSlider.setMax(23);
        hourSlider.setBlockIncrement(1);
        hourSlider.setShowTickMarks(true);
        hourSlider.setSnapToTicks(true);
        hourSlider.setMinorTickCount(0);
        hourSlider.setShowTickLabels(true);
        hourSlider.setMajorTickUnit(1);

        minuteSlider.setMax(55);
        minuteSlider.setMin(0);
        minuteSlider.setBlockIncrement(5);
        minuteSlider.setMinorTickCount(1);
        minuteSlider.setShowTickLabels(true);
        minuteSlider.setSnapToTicks(true);
        minuteSlider.setShowTickMarks(true);
        minuteSlider.setMajorTickUnit(10);

    }



    public void onSliderMove(MouseEvent mouseEvent) {
        this.hour = (int)hourSlider.getValue();
        this.minutes = (int) minuteSlider.getValue();
        timeText.setText(this.hour + ":" + this.minutes);
    }


    public void onClickBtnArrange(ActionEvent actionEvent) {
       LocalDate localDate = datePicker.getValue();
        if(minutes == null || hour == null || localDate == null){
            lblArangeError.setText("Set date, hours and minutes!");
            return;
        }
        //        convert date to millis
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long timeInMillis = instant.toEpochMilli();
        int timeInSec = (int) (timeInMillis / 1000);
        timeInSec += hour * 3600;
        timeInSec += minutes * 60;
        if((int)(new Date().getTime()/1000) > timeInSec){
            lblArangeError.setText("Arranged time not in the future");
            return;
        }
        NewMsgController.passDate(NewMsgController.currentController, timeInSec);
        Stage stage = (Stage) btnArrange.getScene().getWindow();
        stage.close();
    }
}
