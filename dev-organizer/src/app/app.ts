import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Inbox} from './inbox/inbox';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

}
