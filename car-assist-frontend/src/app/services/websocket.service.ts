import { Injectable } from '@angular/core';
import {Observable, Subject} from 'rxjs';
import { Client, IMessage, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {env} from '../../env/env';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  private client: Client;
  private connected: boolean = false;
  private messageSubject = new Subject<string>();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS(env.ws),
      reconnectDelay: 5000,
    });

    this.client.onConnect = (frame) => {
      console.log('Connected: ', frame);
      this.connected = true;
    };

    this.client.onStompError = (frame) => {
      console.error('Broker error: ', frame);
    };

    this.client.activate();
  }

  subscribeToTopic(topic: string): Observable<string> {
    const subject = new Subject<string>();

    if (!this.connected) {
      this.client.onConnect = () => {
        this.connected = true;
        this.doSubscribe(topic, subject);
      };
    } else {
      this.doSubscribe(topic, subject);
    }

    return subject.asObservable();
  }

  private doSubscribe(topic: string, subject: Subject<string>) {
    this.client.subscribe(topic, (message: IMessage) => {
      subject.next(message.body);
    });
  }

  sendMessage(message: string) {
    if (this.client.connected) {
      this.client.publish({
        destination: '/app/hello',
        body: message,
      });
    }
  }

  get messages$(): Observable<string> {
    return this.messageSubject.asObservable();
  }
}
