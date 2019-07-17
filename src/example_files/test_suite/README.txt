S6_all_concat_test
EventW_Machine
- 86 instances of pedestrian alarm
- 25 instances of truck detected
- 52 instanes of obstacle detected
- 21 instances of false alarm
- 44 instances of cyclist detected
Mode_H2M
- 153 instances of manual
- 2705 instances of autonomous


S6_all_concat_test_brief (100 lines)
EventW_Machine
- 98 instances of " "
- instances of
- instances of
Mode_H2M
Trust delta:=10
- 48 instances of 0
- 19 instances of 1
- 6 instances of 2
- 9 instances of 3
- 9 instances of -1
- 5 instances of -3
- 4 instances of -4
41 instances of trust delta :=0 given 49 instances of EventW_Machine== (space)


S6_all_concat_long (2858 lines)
Trust delta:=10
- positive_count: 642
- negative_count: 556
- zero_count: 1660
EventW_Machine
- 86 instances of pedestrian alarm
- 25 instances of truck detected
- 52 instanes of obstacle detected
- 21 instances of false alarm
- 44 instances of cyclist detected

AVERAGE PROBABILITIES FOR ALL USERS
Frequency count found using Csv_Counter.java
Alarm_M2H: =0.936
Alarm_M2H:pedestrian alarm=0.027
Alarm_M2H:cyclist alarm=0.016
Alarm_M2H:obstacle alarm=0.014
Alarm_M2H:truck alarm=0.007
Mode_H2M:autonomous=0.834
Mode_H2M:manual=0.166
EventW_Machine: =0.918
EventW_Machine:pedestrian detected=0.034
EventW_Machine:cyclist detected=0.015
EventW_Machine:truck detected=0.008
EventW_Machine:false alarm=0.007
EventW_Machine:obstacle detected=0.017
EventW_Machine:EventW_Machine!=false alarm&&EventW_Machine!= =0.074
ManualGear_H2M:0=0.002
ManualGear_H2M:1=0.993
ManualGear_H2M:2=0.003
ManualGear_H2M:3=0.002
ManualGear_H2M:4=0.001
ManualGear_H2M:5=0.001
Speed_Machine:Speed_Machine<5=0.059
Speed_Machine:Speed_Machine>=10&&Speed_Machine<15=0.249
Speed_Machine:Speed_Machine>=5&&Speed_Machine<10=0.110
Speed_Machine:Speed_Machine>=15=0.583
PupilRight_Human:6.7=0.000
TrustDelta:Trust==0:=0.765
TrustDelta:Trust>0=0.121
TrustDelta:Trust<0=0.115
CurrentWheel_MachineDelta:CurrentWheel_Machine>=2.5||CurrentWheel_Machine<=-2.5=0.751
CurrentWheel_MachineDelta:CurrentWheel_Machine<2.5&&CurrentWheel_Machine>-2.5=0.249

