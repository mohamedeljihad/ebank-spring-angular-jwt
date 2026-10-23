import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../services/chat.service';

interface ChatMessage { from: 'user' | 'bot'; text: string; }

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.css'
})
export class ChatbotComponent {
  messages: ChatMessage[] = [
    { from: 'bot', text: "Bonjour ! Je suis l'assistant E-Bank. Je peux répondre sur nos services (frais, types de comptes, procédures) et consulter les comptes des clients." }
  ];
  question = '';
  loading = false;
  suggestions = ['Quels sont les frais du compte courant ?', 'Quels sont les comptes du client Hassan ?', 'Donne-moi les statistiques de la banque'];

  constructor(private chatService: ChatService) {}

  send(text = this.question): void {
    const q = text.trim();
    if (!q || this.loading) return;
    this.messages.push({ from: 'user', text: q });
    this.question = '';
    this.loading = true;
    this.chatService.ask(q).subscribe({
      next: r => this.messages.push({ from: 'bot', text: r.answer }),
      error: err => this.messages.push({ from: 'bot', text: 'Erreur : ' + (err.error?.message ?? err.message) }),
      complete: () => (this.loading = false)
    });
  }
}
