# Next.js Project Setup Guide

This is a [Next.js](https://nextjs.org/) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/getting-started) and uses [shadcn/ui](https://ui.shadcn.com/) for UI components.

## Prerequisites

Before you begin, ensure you have the following installed:
- [Node.js](https://nodejs.org/) (version 16.14 or higher)
- [pnpm](https://pnpm.io/) (recommended package manager)

## Getting Started

1. Clone the repository:

```bash
git clone <your-repository-url>
cd <project-directory>
```

2. Install dependencies:

```bash
pnpm install
```

3. Initialize shadcn/ui:

```bash
pnpm dlx shadcn@latest init
Which style would you like to use? › New York
Which color would you like to use as base color? › Zinc
Do you want to use CSS variables for colors? › Yes
```

4. Run the development server:

```bash
pnpm run dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

## Project Structure

```
├── app/                   # Next.js 13+ app directory
├── components/           # UI components including shadcn components
├── public/              # Static assets
```

## Available Scripts

- `pnpm dev` - Runs the development server
- `pnpm build` - Builds the application for production
- `pnpm start` - Runs the production server
- `pnpm lint` - Runs ESLint for code linting

## Adding New Dependencies

To add new packages to the project:

```bash
pnpm add <package-name>
```

## Adding shadcn/ui Components

To add new shadcn/ui components:

```bash
$ pnpm dlx shadcn@latest add <component-name>
```

## Deployment

### Vercel (Recommended)

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new).

1. Push your code to a Git repository
2. Import your project to Vercel
3. Vercel will detect Next.js automatically and configure the build settings

### Self-hosted Deployment

1. Build the application:
```bash
pnpm build
```

2. Start the production server:
```bash
pnpm start
```

The application will be available on port 3000 by default.

## Environment Variables

Create a `.env.local` file in the root directory and add your environment variables:

```env
NEXT_PUBLIC_API_URL=your_api_url_here
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Learn More

- [Next.js Documentation](https://nextjs.org/docs)
- [shadcn/ui Documentation](https://ui.shadcn.com)
- [pnpm Documentation](https://pnpm.io/motivation)
