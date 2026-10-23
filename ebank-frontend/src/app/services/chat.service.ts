import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ChatService {
  constructor(private http: HttpClient) {}

  ask(query: string): Observable<{ question: string; answer: string }> {
    return this.http.get<{ question: string; answer: string }>(
      `${environment.backendHost}/chat?query=${encodeURIComponent(query)}`);
  }
}
