import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'AutoTagAuditor';
  auditHistory = signal<any[]>([]); // To Store the Data we get back

  //Inject the HttpClient
  constructor(private http: HttpClient) {}

  auditUrl(url: string) {
    if (!url) {
      alert('Please enter a valid URL');
      return;
    }

    console.log("Sending request for: ", url);

    //Call the Kotlin backend
    //Expect the JSON response back format: { "url": "...", "tagsFound": [...], "status": "..." }
    this.http.get(`http://localhost:8080/audit?url=${url}`)
      .subscribe({
        next: (data) => {
          console.log("Data received: ", data);
          this.auditHistory.update(auditHistory => [data, ...auditHistory]);
        },
        error: (err) => {
          console.error("Error: ", err);
          alert("Error occurred while auditing the URL.");
        }
      });
  }
}
