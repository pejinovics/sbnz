import {Component, OnInit} from '@angular/core';
import {WebsocketService} from '../../services/websocket.service';
import {JsonPipe, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {SimulationService} from '../../services/simulation.service';

interface Rule {
  ruleName: string;
  facts: any[];
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    JsonPipe,
    NgForOf,
    NgIf,
    FormsModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  firedRules: Rule[] = [];

  simulations = [
    { label: 'Air Condition', value: 'aircondition' },
    { label: 'Motor System', value: 'motorsystem' },
    { label: 'Tyre Pressure', value: 'tyrepressure' },
    { label: 'Gear Box', value: 'gearbox' },
    { label: 'Fuel Consumption (CEP)', value: 'fuelconsumption' }
  ];

  selectedSimulation: string = 'aircondition';

  constructor(private wsService: WebsocketService,
              private http: HttpClient,
              private simulationService: SimulationService) {}

  ngOnInit(): void {
    this.wsService.subscribeToTopic('/topic/rules').subscribe((msg) => {
      const parsed = JSON.parse(msg);
      this.firedRules.unshift(parsed);
    });
  }


  startSimulation() {
    this.firedRules = [];
    this.simulationService.startSimulation(this.selectedSimulation).subscribe({
      next: res => console.log('Simulacija pokrenuta:', res),
      error: err => console.error('Greška pri pokretanju:', err)
    });
  }
}
