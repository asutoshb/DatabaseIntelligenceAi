# AI Database Intelligence Platform - Frontend

React + TypeScript frontend for the AI Database Intelligence Platform.

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ 
- npm or yarn

### Installation

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   # Using npm:
   npm install
   # OR using yarn:
   yarn install
   ```

3. **Start development server:**
   ```bash
   # Using npm:
   npm run dev
   # OR using yarn:
   yarn dev
   ```

4. **Open browser:**
   ```
   http://localhost:3000
   ```

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/      # Reusable UI components
│   ├── pages/           # Page components
│   ├── services/        # API service functions
│   ├── hooks/           # Custom React hooks
│   ├── types/           # TypeScript type definitions
│   ├── App.tsx          # Main app component
│   ├── main.tsx         # Entry point
│   └── index.css        # Global styles
├── public/              # Static assets
├── package.json         # Dependencies
├── vite.config.ts       # Vite configuration
└── tsconfig.json        # TypeScript configuration
```

## 📚 Tech Stack (Chunk 1)

- **React 18**: JavaScript library for building user interfaces
- **TypeScript**: Typed JavaScript (catches errors before runtime)
- **Vite**: Fast build tool and dev server
- **Material-UI**: Beautiful React component library
- **Axios**: For making HTTP requests to backend

## 🔧 Available Scripts

**Using npm:**
- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

**Using yarn:**
- `yarn dev` - Start development server
- `yarn build` - Build for production
- `yarn preview` - Preview production build
- `yarn lint` - Run ESLint

## 🌐 API Connection

The frontend is configured to connect to backend at:
- Development: `http://localhost:8080/api`
- Configured in `vite.config.ts` (proxy) and `App.tsx` (axios calls)

## 🎨 UI Components

Currently using Material-UI components:
- Typography (text)
- Container (layout)
- Paper (card-like container)
- Button (buttons)
- Box (spacing/layout)

More components will be added in future chunks!

