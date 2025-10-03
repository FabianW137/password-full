import { bootstrapApplication } from '@angular/platform-browser';
import { Component, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';

async function api(path: string, options: any = {}, token?: string){
  const headers:any = { 'Content-Type':'application/json' };
  if(token) headers['Authorization'] = 'Bearer ' + token;
  const res = await fetch(path, { ...options, headers });
  if(!res.ok) throw new Error(await res.text());
  const ct = res.headers.get('content-type')||''; return ct.includes('application/json') ? res.json() : res.text();
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  template: `
  <h1>Passwortmanager (Angular SPA)</h1>

  <div *ngIf="!jwt()" class="card">
    <h3>Login</h3>
    <form (ngSubmit)="startLogin()">
      <input [(ngModel)]="email" name="email" type="email" placeholder="E-Mail" required>
      <input [(ngModel)]="password" name="password" type="password" placeholder="Passwort" required>
      <button>Weiter</button>
    </form>
    <div *ngIf="tmpToken">
      <h4>TOTP</h4>
      <input [(ngModel)]="totp" name="totp" placeholder="123456" maxlength="6">
      <button (click)="verifyTotp()">Bestätigen</button>
    </div>
  </div>

  <div *ngIf="jwt()" class="card">
    <div class="row"><div class="label">Status</div><div class="value"><button (click)="logout()">Logout</button></div></div>
    <div class="row"><div class="label">Suche</div><input [(ngModel)]="q" (input)="applyFilter()" placeholder="Suchen…"></div>
    <div class="row"><div class="label">Neu</div>
      <input [(ngModel)]="form.title" placeholder="Titel">
      <input [(ngModel)]="form.username" placeholder="Benutzer">
      <input [(ngModel)]="form.password" placeholder="Passwort">
      <input [(ngModel)]="form.url" placeholder="URL">
      <button (click)="create()">+ Hinzufügen</button>
    </div>

    <div *ngIf="filtered().length===0" class="card">Keine Einträge</div>
    <div *ngFor="let it of filtered()" class="card">
      <div class="row"><div class="label">Titel</div><div class="value">{{it.title}}</div></div>
      <div class="row"><div class="label">Benutzer</div><div class="value">{{it.username}}</div></div>
      <div class="row"><div class="label">Passwort</div><div class="value"><code>{{mask(it.password||'')}}</code></div></div>
      <div class="row"><div class="label">URL</div><div class="value">{{it.url || '—'}}</div></div>
      <div class="row"><div class="label">Notizen</div><div class="value">{{it.notes || '—'}}</div></div>
      <button (click)="remove(it.id)">Löschen</button>
    </div>
  </div>
  `
})
class AppComponent {
  email=''; password=''; totp=''; tmpToken='';
  jwt = signal<string | null>(localStorage.getItem('jwt'));
  items = signal<any[]>([]);
  q = ''; form:any = { title:'', username:'', password:'', url:'', notes:'' };
  filtered = computed(()=> this.items().filter(x => !this.q || (x.title||'').toLowerCase().includes(this.q.toLowerCase()) || (x.username||'').toLowerCase().includes(this.q.toLowerCase()) || (x.url||'').toLowerCase().includes(this.q.toLowerCase())) );

  mask(s:string){ return '•'.repeat(Math.min(10, s.length)); }

  async startLogin(){ const r = await api('/api/auth/login',{method:'POST',body:JSON.stringify({email:this.email,password:this.password})}); this.tmpToken=r.tmpToken; }
  async verifyTotp(){ const v = await api('/api/auth/totp-verify',{method:'POST',body:JSON.stringify({tmpToken:this.tmpToken,code:this.totp})}); this.tmpToken=''; localStorage.setItem('jwt', v.token); this.jwt.set(v.token); this.load(); }
  logout(){ localStorage.removeItem('jwt'); this.jwt.set(null); this.items.set([]); }
  async load(){ const data = await api('/api/vault',{}, this.jwt()!); this.items.set(data); }
  async create(){ const it = await api('/api/vault',{method:'POST',body:JSON.stringify(this.form)}, this.jwt()!); this.items.set([it, ...this.items()]); this.form={title:'',username:'',password:'',url:'',notes:''}; }
  async remove(id:string){ await api('/api/vault/'+id,{method:'DELETE'}, this.jwt()!); this.items.set(this.items().filter(x=>x.id!==id)); }
  applyFilter(){ /* computed handles it */ }
  constructor(){ if(this.jwt()) this.load(); }
}

bootstrapApplication(AppComponent);
