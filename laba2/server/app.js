const express = require('express');
const cors = require('cors');
const app = express();
 
app.use(cors());
app.use(express.json());
 
let tasks = [
  { id: 1, title: 'Вивчити Node.js', done: false },
  { id: 2, title: 'Зробити лабораторну роботу', done: false },
];
 
app.get('/tasks', (req, res) => {
  res.json(tasks);
});
 
app.post('/tasks', (req, res) => {
  const t = { id: Date.now(), title: req.body.title, done: false };
  tasks.push(t);
  res.json(t);
});
 
app.delete('/tasks/:id', (req, res) => {
  tasks = tasks.filter(t => t.id !== Number(req.params.id));
  res.json({ ok: true });
});
 
app.listen(3001, () => console.log('Сервер на http://localhost:3001'));