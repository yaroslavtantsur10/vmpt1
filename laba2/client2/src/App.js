import { useState, useEffect } from "react";

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [newTask, setNewTask] = useState("");

  useEffect(() => {
    fetch("http://localhost:3001/tasks")
      .then((res) => res.json())
      .then((data) => setTasks(data));
  }, []);

  function addTask() {
    if (!newTask.trim()) return;
    fetch("http://localhost:3001/tasks", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title: newTask }),
    })
      .then((res) => res.json())
      .then((task) => {
        setTasks([...tasks, task]);
        setNewTask("");
      });
  }

  function deleteTask(id) {
    fetch(`http://localhost:3001/tasks/${id}`, { method: "DELETE" }).then(() =>
      setTasks(tasks.filter((t) => t.id !== id))
    );
  }

  return (
    <div style={{ maxWidth: 500, margin: "60px auto", fontFamily: "sans-serif" }}>
      <h1>Список задач</h1>
      <div style={{ display: "flex", gap: 8, marginBottom: 24 }}>
        <input
          value={newTask}
          onChange={(e) => setNewTask(e.target.value)}
          placeholder="Нова задача..."
          style={{ flex: 1, padding: "8px 12px", fontSize: 16 }}
        />
        <button onClick={addTask} style={{ padding: "8px 16px", fontSize: 16 }}>
          Додати
        </button>
      </div>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {tasks.map((task) => (
          <li
            key={task.id}
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              padding: "12px 16px",
              marginBottom: 8,
              background: "#f5f5f5",
              borderRadius: 6,
            }}
          >
            <span>{task.title}</span>
            <button
              onClick={() => deleteTask(task.id)}
              style={{
                background: "#e53935",
                color: "#fff",
                border: "none",
                padding: "4px 10px",
                borderRadius: 4,
                cursor: "pointer",
              }}
            >
              Видалити
            </button>
          </li>
        ))}
      </ul>
      {tasks.length === 0 && <p style={{ color: "#999" }}>Задач немає</p>}
    </div>
  );
}