import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Duration, DurationObjectUnits} from 'luxon';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-duration-picker-component',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule
  ],
  templateUrl: './duration-picker-component.html',
  styleUrl: './duration-picker-component.css'
})
export class DurationPickerComponent {

  @Input() set isoDurationString(value: string) {
    this.duration = Duration.fromISO(value);
  }

  @Output() isoDurationStringChange: EventEmitter<string>  = new EventEmitter<string>();

  duration: Duration = Duration.fromMillis(0);

  set hours(value: number) {
    const durationObject = this.duration.toObject();
    durationObject.hours = value;
    this.updateDurationFromObject(durationObject);
  }
  get hours(): number {
    return this.duration.hours;
  }

  set minutes(value: number) {
    const durationObject = this.duration.toObject();
    durationObject.minutes = value;
    this.updateDurationFromObject(durationObject);
  }
  get minutes(): number {
    return this.duration.minutes;
  }

  private updateDurationFromObject(durationObject: DurationObjectUnits) {
    this.duration = Duration.fromObject(durationObject);

    const isoString = this.duration.toISO();
    if (isoString)
      this.isoDurationStringChange.emit(isoString);
  }

  constructor() {

  }


}
